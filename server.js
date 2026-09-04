const express = require("express");
const multer = require("multer");

const app = express();

/* =========================================================
   CONFIGURATION
========================================================= */

const PORT = process.env.PORT || 3000;

const GEMINI_API_KEY = process.env.GEMINI_API_KEY;

const GEMINI_URL =
  "https://generativelanguage.googleapis.com/v1beta/interactions";

/*
 * Primary model.
 * If this model temporarily fails, the server will try
 * the fallback model automatically.
 */
const PRIMARY_MODEL = "gemini-3.8-flash";

const FALLBACK_MODEL = "gemini-2.5-flash-lite";

/* =========================================================
   MIDDLEWARE
========================================================= */

app.use(express.json({ limit: "10mb" }));

/* =========================================================
   CORS
========================================================= */

app.use((req, res, next) => {
  res.header("Access-Control-Allow-Origin", "*");

  res.header(
    "Access-Control-Allow-Headers",
    "Origin, X-Requested-With, Content-Type, Accept"
  );

  res.header(
    "Access-Control-Allow-Methods",
    "GET, POST, OPTIONS"
  );

  if (req.method === "OPTIONS") {
    return res.sendStatus(200);
  }

  next();
});

/* =========================================================
   MULTER
========================================================= */

const upload = multer({
  storage: multer.memoryStorage(),

  limits: {
    fileSize: 10 * 1024 * 1024,
  },
});

/* =========================================================
   GEMINI API KEY CHECK
========================================================= */

if (!GEMINI_API_KEY) {
  console.error("==============================================");
  console.error("ERROR: GEMINI_API_KEY IS MISSING");
  console.error("Add GEMINI_API_KEY to Render Environment Variables.");
  console.error("==============================================");
}

/* =========================================================
   HOME
========================================================= */

app.get("/", (req, res) => {
  res.json({
    status: "online",
    server: "Smart Farm Disease AI",
    endpoint: "/analyze-disease",
    health: "/health",
    primaryModel: PRIMARY_MODEL,
    fallbackModel: FALLBACK_MODEL,
  });
});

/* =========================================================
   HEALTH CHECK
========================================================= */

app.get("/health", (req, res) => {
  res.json({
    status: "healthy",
    server: "Smart Farm Disease AI",
    model: PRIMARY_MODEL,
    fallbackModel: FALLBACK_MODEL,
  });
});

/* =========================================================
   DISEASE RESPONSE SCHEMA
========================================================= */

const diseaseSchema = {
  type: "object",

  properties: {
    crop: {
      type: "string",
    },

    disease: {
      type: "string",
    },

    confidence: {
      type: "number",
    },

    symptoms: {
      type: "string",
    },

    causes: {
      type: "string",
    },

    treatment: {
      type: "string",
    },

    prevention: {
      type: "string",
    },
  },

  required: [
    "crop",
    "disease",
    "confidence",
    "symptoms",
    "causes",
    "treatment",
    "prevention",
  ],
};

/* =========================================================
   AI PROMPT
========================================================= */

const DISEASE_PROMPT = `
You are an expert agricultural plant disease detection AI.

Analyze the uploaded crop or plant image carefully.

Your task is to identify:

1. Crop name
2. Disease or health condition
3. Confidence percentage
4. Visible symptoms
5. Possible causes
6. Recommended treatment
7. Prevention methods

IMPORTANT RULES:

- Analyze the actual uploaded image.
- If the plant appears healthy, disease must be "Healthy".
- If the image is not a crop/plant or cannot be analyzed,
  disease must be "Unable to analyze".
- Do not invent a disease when visual evidence is insufficient.
- Use common agricultural disease names.
- Give practical agricultural treatment advice.
- Confidence must be a number from 0 to 100.
- Return ONLY valid JSON matching the requested schema.
`;

/* =========================================================
   MIME TYPE DETECTION
========================================================= */

function getMimeType(file) {
  let mimeType = file.mimetype;

  if (
    mimeType &&
    mimeType.startsWith("image/") &&
    mimeType !== "application/octet-stream"
  ) {
    return mimeType;
  }

  const extension = file.originalname
    .split(".")
    .pop()
    .toLowerCase();

  const mimeMap = {
    jpg: "image/jpeg",
    jpeg: "image/jpeg",
    png: "image/png",
    webp: "image/webp",
    gif: "image/gif",
    bmp: "image/bmp",
  };

  return mimeMap[extension] || "image/jpeg";
}

/* =========================================================
   GEMINI REQUEST
========================================================= */

async function callGemini({
  model,
  base64Image,
  mimeType,
}) {
  const requestBody = {
    model: model,

    store: false,

    input: [
      {
        type: "image",
        mime_type: mimeType,
        data: base64Image,
      },

      {
        type: "text",
        text: DISEASE_PROMPT,
      },
    ],

    response_format: {
      type: "text",
      mime_type: "application/json",
      schema: diseaseSchema,
    },

    generation_config: {
      thinking_level: "minimal",
      max_output_tokens: 2000,
    },
  };

  console.log("");
  console.log("----------------------------------------------");
  console.log("Calling Gemini");
  console.log("Model:", model);
  console.log("----------------------------------------------");

  const response = await fetch(GEMINI_URL, {
    method: "POST",

    headers: {
      "Content-Type": "application/json",
      "x-goog-api-key": GEMINI_API_KEY,
    },

    body: JSON.stringify(requestBody),
  });

  const text = await response.text();

  console.log("Gemini HTTP status:", response.status);

  if (!response.ok) {
    let errorMessage = "Gemini request failed.";

    try {
      const errorData = JSON.parse(text);

      if (errorData?.error?.message) {
        errorMessage = errorData.error.message;
      } else if (errorData?.message) {
        errorMessage = errorData.message;
      }
    } catch (_) {
      if (text) {
        errorMessage = text;
      }
    }

    const error = new Error(errorMessage);

    error.status = response.status;

    throw error;
  }

  let data;

  try {
    data = JSON.parse(text);
  } catch (_) {
    throw new Error("Gemini returned invalid JSON response.");
  }

  return data;
}

/* =========================================================
   EXTRACT MODEL TEXT
========================================================= */

function extractAIText(geminiData) {
  let aiText = "";

  /*
   * Current Interactions API response.
   */
  if (Array.isArray(geminiData.steps)) {
    for (const step of geminiData.steps) {
      if (!step) {
        continue;
      }

      if (
        step.type === "model_output" &&
        Array.isArray(step.content)
      ) {
        for (const content of step.content) {
          if (typeof content?.text === "string") {
            aiText += content.text;
          }
        }
      }
    }
  }

  /*
   * output_text fallback.
   */
  if (
    !aiText &&
    typeof geminiData.output_text === "string"
  ) {
    aiText = geminiData.output_text;
  }

  /*
   * outputs fallback.
   */
  if (!aiText && Array.isArray(geminiData.outputs)) {
    for (const output of geminiData.outputs) {
      if (!output) {
        continue;
      }

      if (typeof output.text === "string") {
        aiText += output.text;
      }

      if (Array.isArray(output.content)) {
        for (const content of output.content) {
          if (typeof content?.text === "string") {
            aiText += content.text;
          }
        }
      }
    }
  }

  /*
   * Simple text fallback.
   */
  if (
    !aiText &&
    typeof geminiData.text === "string"
  ) {
    aiText = geminiData.text;
  }

  return aiText.trim();
}

/* =========================================================
   CLEAN AI JSON
========================================================= */

function parseAIResult(aiText) {
  if (!aiText) {
    throw new Error(
      "Gemini returned an empty disease analysis."
    );
  }

  let cleaned = aiText
    .replace(/```json/gi, "")
    .replace(/```/g, "")
    .trim();

  /*
   * Find JSON object if Gemini adds extra text.
   */
  const firstBrace = cleaned.indexOf("{");
  const lastBrace = cleaned.lastIndexOf("}");

  if (
    firstBrace !== -1 &&
    lastBrace !== -1 &&
    lastBrace > firstBrace
  ) {
    cleaned = cleaned.substring(
      firstBrace,
      lastBrace + 1
    );
  }

  return JSON.parse(cleaned);
}

/* =========================================================
   NORMALIZE RESULT
========================================================= */

function normalizeResult(result) {
  let confidence = Number(result.confidence);

  if (Number.isNaN(confidence)) {
    confidence = 0;
  }

  /*
   * Sometimes AI returns 0.95 instead of 95.
   */
  if (confidence > 0 && confidence <= 1) {
    confidence = confidence * 100;
  }

  confidence = Math.max(
    0,
    Math.min(100, confidence)
  );

  return {
    crop: String(
      result.crop || "Unknown"
    ),

    disease: String(
      result.disease || "Unable to analyze"
    ),

    confidence: Number(
      confidence.toFixed(1)
    ),

    symptoms: String(
      result.symptoms ||
        "No information available."
    ),

    causes: String(
      result.causes ||
        "No information available."
    ),

    treatment: String(
      result.treatment ||
        "No information available."
    ),

    prevention: String(
      result.prevention ||
        "No information available."
    ),
  };
}

/* =========================================================
   SHOULD RETRY
========================================================= */

function shouldRetry(error) {
  const status = Number(error.status || 0);

  const message = String(
    error.message || ""
  ).toLowerCase();

  /*
   * Temporary server/rate-limit conditions.
   */
  if (
    status === 429 ||
    status === 500 ||
    status === 502 ||
    status === 503 ||
    status === 504
  ) {
    return true;
  }

  /*
   * Gemini high-demand message.
   */
  if (
    message.includes("high demand") ||
    message.includes("temporarily") ||
    message.includes("try again later") ||
    message.includes("overloaded")
  ) {
    return true;
  }

  return false;
}

/* =========================================================
   WAIT
========================================================= */

function sleep(ms) {
  return new Promise((resolve) => {
    setTimeout(resolve, ms);
  });
}

/* =========================================================
   GEMINI WITH RETRY + FALLBACK
========================================================= */

async function analyzeWithGemini({
  base64Image,
  mimeType,
}) {
  let lastError = null;

  /*
   * -------------------------------------------------------
   * PRIMARY MODEL
   * -------------------------------------------------------
   */

  for (let attempt = 1; attempt <= 2; attempt++) {
    try {
      console.log(
        `Primary model attempt ${attempt}/2`
      );

      const data = await callGemini({
        model: PRIMARY_MODEL,
        base64Image,
        mimeType,
      });

      return data;
    } catch (error) {
      lastError = error;

      console.error(
        "Primary model failed:",
        error.message
      );

      if (!shouldRetry(error)) {
        break;
      }

      if (attempt < 2) {
        console.log(
          "Temporary error. Retrying primary model..."
        );

        await sleep(1500);
      }
    }
  }

  /*
   * -------------------------------------------------------
   * FALLBACK MODEL
   * -------------------------------------------------------
   */

  console.log(
    "Trying fallback model:",
    FALLBACK_MODEL
  );

  try {
    const data = await callGemini({
      model: FALLBACK_MODEL,
      base64Image,
      mimeType,
    });

    return data;
  } catch (error) {
    lastError = error;

    console.error(
      "Fallback model failed:",
      error.message
    );
  }

  throw lastError ||
    new Error("All Gemini models failed.");
}

/* =========================================================
   DISEASE DETECTION
========================================================= */

app.post(
  "/analyze-disease",
  upload.single("image"),
  async (req, res) => {
    console.log("");
    console.log("==============================================");
    console.log("SMART FARM DISEASE AI");
    console.log("==============================================");

    try {
      /*
       * ---------------------------------------------------
       * API KEY
       * ---------------------------------------------------
       */

      if (!GEMINI_API_KEY) {
        return res.status(500).json({
          error:
            "Gemini API key is not configured on the server.",
        });
      }

      /*
       * ---------------------------------------------------
       * IMAGE
       * ---------------------------------------------------
       */

      if (!req.file) {
        console.log(
          "ERROR: No image received"
        );

        return res.status(400).json({
          error:
            "No image uploaded. Please send an image.",
        });
      }

      console.log(
        "Image received:",
        req.file.originalname
      );

      console.log(
        "Image size:",
        req.file.size,
        "bytes"
      );

      console.log(
        "Image type:",
        req.file.mimetype
      );

      /*
       * ---------------------------------------------------
       * MIME TYPE
       * ---------------------------------------------------
       */

      const mimeType = getMimeType(req.file);

      console.log(
        "Using MIME type:",
        mimeType
      );

      /*
       * ---------------------------------------------------
       * BASE64
       * ---------------------------------------------------
       */

      const base64Image =
        req.file.buffer.toString("base64");

      console.log(
        "Image converted to Base64."
      );

      console.log(
        "Base64 length:",
        base64Image.length
      );

      /*
       * ---------------------------------------------------
       * GEMINI
       * ---------------------------------------------------
       */

      const geminiData =
        await analyzeWithGemini({
          base64Image,
          mimeType,
        });

      /*
       * ---------------------------------------------------
       * EXTRACT TEXT
       * ---------------------------------------------------
       */

      const aiText =
        extractAIText(geminiData);

      console.log("");
      console.log("Gemini AI response:");
      console.log(aiText);

      /*
       * ---------------------------------------------------
       * PARSE JSON
       * ---------------------------------------------------
       */

      let parsedResult;

      try {
        parsedResult =
          parseAIResult(aiText);
      } catch (error) {
        console.error(
          "AI JSON parsing failed:"
        );

        console.error(
          error.message
        );

        console.error(
          "Raw AI text:",
          aiText
        );

        return res.status(502).json({
          error:
            "AI returned invalid disease analysis data.",
        });
      }

      /*
       * ---------------------------------------------------
       * NORMALIZE
       * ---------------------------------------------------
       */

      const finalResult =
        normalizeResult(parsedResult);

      /*
       * ---------------------------------------------------
       * SUCCESS
       * ---------------------------------------------------
       */

      console.log("");
      console.log("==============================================");
      console.log("DISEASE AI SUCCESS");
      console.log("==============================================");

      console.log(
        "CROP:",
        finalResult.crop
      );

      console.log(
        "DISEASE:",
        finalResult.disease
      );

      console.log(
        "CONFIDENCE:",
        finalResult.confidence + "%"
      );

      console.log("==============================================");
      console.log("");

      return res.status(200).json(
        finalResult
      );
    } catch (error) {
      /*
       * ---------------------------------------------------
       * ERROR HANDLING
       * ---------------------------------------------------
       */

      console.error("");
      console.error("==============================================");
      console.error("DISEASE AI ERROR");
      console.error("==============================================");

      console.error(
        "Status:",
        error.status || "unknown"
      );

      console.error(
        "Error:",
        error.message
      );

      console.error("==============================================");
      console.error("");

      const status = Number(
        error.status || 0
      );

      /*
       * Rate limit
       */
      if (status === 429) {
        return res.status(429).json({
          error:
            "Gemini API quota or rate limit exceeded. Please try again later.",
        });
      }

      /*
       * Temporary Gemini overload
       */
      if (
        status === 500 ||
        status === 502 ||
        status === 503 ||
        status === 504 ||
        String(error.message)
          .toLowerCase()
          .includes("high demand")
      ) {
        return res.status(502).json({
          error:
            "Gemini AI is temporarily unavailable. Please try again in a moment.",
        });
      }

      return res.status(500).json({
        error:
          "Disease detection failed.",
        details:
          error.message,
      });
    }
  }
);

/* =========================================================
   404 HANDLER
========================================================= */

app.use((req, res) => {
  res.status(404).json({
    error: "Endpoint not found.",

    availableEndpoints: [
      "GET /",
      "GET /health",
      "POST /analyze-disease",
    ],
  });
});

/* =========================================================
   START SERVER
========================================================= */

app.listen(
  PORT,
  "0.0.0.0",
  () => {
    console.log("");
    console.log("==============================================");
    console.log("       SMART FARM DISEASE AI SERVER");
    console.log("==============================================");

    console.log(
      "Server running on port:",
      PORT
    );

    console.log(
      "Endpoint: POST /analyze-disease"
    );

    console.log(
      "Health: GET /health"
    );

    console.log(
      "Primary model:",
      PRIMARY_MODEL
    );

    console.log(
      "Fallback model:",
      FALLBACK_MODEL
    );

    console.log(
      "Gemini API:",
      GEMINI_API_KEY
        ? "CONFIGURED"
        : "NOT CONFIGURED"
    );

    console.log("==============================================");
    console.log("");
  }
);
