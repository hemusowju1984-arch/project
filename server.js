require("dotenv").config();

const express = require("express");
const multer = require("multer");

const app = express();

/* =========================================================
   CONFIGURATION
========================================================= */

// Render provides PORT automatically.
// Local computer will use 3000.
const PORT = process.env.PORT || 3000;

// Gemini model
const GEMINI_MODEL = "gemini-3.5-flash-lite";

// Gemini Interactions API
const GEMINI_URL =
  "https://generativelanguage.googleapis.com/v1beta/interactions";

/* =========================================================
   MIDDLEWARE
========================================================= */

app.use(express.json({ limit: "10mb" }));

// Allow requests from Flutter/web applications.
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
   MULTER - IMAGE UPLOAD
========================================================= */

const upload = multer({
  storage: multer.memoryStorage(),

  limits: {
    fileSize: 10 * 1024 * 1024, // 10 MB
  },
});

/* =========================================================
   CHECK GEMINI API KEY
========================================================= */

const GEMINI_API_KEY = process.env.GEMINI_API_KEY;

if (!GEMINI_API_KEY) {
  console.error("==============================================");
  console.error("ERROR: GEMINI_API_KEY is missing");
  console.error("==============================================");
}

/* =========================================================
   HOME / HEALTH CHECK
========================================================= */

app.get("/", (req, res) => {
  res.json({
    message: "Smart Farm Disease AI Server is running",
    status: "OK",
    model: GEMINI_MODEL,
    service: "Cloud Disease Detection API",
  });
});

/* =========================================================
   HEALTH CHECK
========================================================= */

app.get("/health", (req, res) => {
  res.json({
    status: "healthy",
    server: "Smart Farm Disease AI",
    model: GEMINI_MODEL,
  });
});

/* =========================================================
   DISEASE DETECTION
========================================================= */

app.post("/analyze-disease", upload.single("image"), async (req, res) => {
  console.log("");
  console.log("==============================================");
  console.log("SMART FARM DISEASE AI");
  console.log("==============================================");

  try {
    /* -----------------------------------------------------
       CHECK API KEY
    ----------------------------------------------------- */

    if (!GEMINI_API_KEY) {
      return res.status(500).json({
        error: "Gemini API key is not configured on the server.",
      });
    }

    /* -----------------------------------------------------
       CHECK IMAGE
    ----------------------------------------------------- */

    if (!req.file) {
      console.log("ERROR: No image received");

      return res.status(400).json({
        error: "No image uploaded. Please send an image.",
      });
    }

    console.log("Image received:");
    console.log("File:", req.file.originalname);
    console.log("Size:", req.file.size, "bytes");
    console.log("Type:", req.file.mimetype);

    /* -----------------------------------------------------
       IMAGE MIME TYPE
    ----------------------------------------------------- */

    let mimeType = req.file.mimetype;

    // Some Android devices send application/octet-stream.
    if (
      !mimeType ||
      mimeType === "application/octet-stream" ||
      !mimeType.startsWith("image/")
    ) {
      const extension =
        req.file.originalname.split(".").pop().toLowerCase();

      const mimeMap = {
        jpg: "image/jpeg",
        jpeg: "image/jpeg",
        png: "image/png",
        webp: "image/webp",
        gif: "image/gif",
        bmp: "image/bmp",
      };

      mimeType = mimeMap[extension] || "image/jpeg";
    }

    /* -----------------------------------------------------
       CONVERT IMAGE TO BASE64
    ----------------------------------------------------- */

    const base64Image = req.file.buffer.toString("base64");

    console.log("Image converted to Base64");
    console.log("Sending image to Gemini...");
    console.log("Model:", GEMINI_MODEL);

    /* -----------------------------------------------------
       AI PROMPT
    ----------------------------------------------------- */

    const prompt = `
You are an expert agricultural plant disease detection AI.

Analyze the uploaded crop/plant image carefully.

Your task is to identify:

1. Crop name
2. Disease or health condition
3. Confidence percentage
4. Visible symptoms
5. Possible causes
6. Recommended treatment
7. Prevention methods

IMPORTANT RULES:

- If the plant appears healthy, disease must be "Healthy".
- If the image is not a crop/plant leaf or is impossible to analyze,
  return disease as "Unable to analyze".
- Do not invent a disease when there is insufficient visual evidence.
- Use common agricultural disease names.
- Give practical treatment advice.
- Confidence must be between 0 and 100.
- Return ONLY valid JSON.
`;

    /* -----------------------------------------------------
       GEMINI REQUEST
    ----------------------------------------------------- */

    const geminiRequest = {
      model: GEMINI_MODEL,

      store: false,

      input: [
        {
          role: "user",

          content: [
            {
              type: "text",
              text: prompt,
            },

            {
              type: "image",
              data: base64Image,
              mime_type: mimeType,
            },
          ],
        },
      ],

      response_format: {
        type: "text",

        mime_type: "application/json",

        schema: {
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
        },
      },

      generation_config: {
        thinking_level: "minimal",
      },
    };

    /* -----------------------------------------------------
       CALL GEMINI
    ----------------------------------------------------- */

    const geminiResponse = await fetch(GEMINI_URL, {
      method: "POST",

      headers: {
        "Content-Type": "application/json",
        "x-goog-api-key": GEMINI_API_KEY,
      },

      body: JSON.stringify(geminiRequest),
    });

    console.log("Gemini HTTP status:", geminiResponse.status);

    const geminiText = await geminiResponse.text();

    /* -----------------------------------------------------
       GEMINI ERROR
    ----------------------------------------------------- */

    if (!geminiResponse.ok) {
      console.error("Gemini error:");
      console.error(geminiText);

      let errorMessage = "Gemini AI request failed.";

      try {
        const errorData = JSON.parse(geminiText);

        if (errorData.error?.message) {
          errorMessage = errorData.error.message;
        }
      } catch (_) {}

      // Quota / rate limit
      if (geminiResponse.status === 429) {
        return res.status(429).json({
          error:
            "Gemini API quota/rate limit exceeded. Please try again later.",
          details: errorMessage,
        });
      }

      return res.status(502).json({
        error: errorMessage,
      });
    }

    /* -----------------------------------------------------
       PARSE GEMINI RESPONSE
    ----------------------------------------------------- */

    let geminiData;

    try {
      geminiData = JSON.parse(geminiText);
    } catch (error) {
      console.error("Could not parse Gemini response");

      return res.status(502).json({
        error: "Gemini returned invalid response data.",
      });
    }

    /* -----------------------------------------------------
       EXTRACT AI TEXT
    ----------------------------------------------------- */

    let aiText = "";

    // Interactions API response normally contains outputs.
    if (Array.isArray(geminiData.outputs)) {
      for (const output of geminiData.outputs) {
        if (!output) continue;

        if (typeof output.text === "string") {
          aiText += output.text;
        }

        if (Array.isArray(output.content)) {
          for (const content of output.content) {
            if (typeof content.text === "string") {
              aiText += content.text;
            }
          }
        }
      }
    }

    // Additional fallback formats
    if (!aiText && typeof geminiData.output_text === "string") {
      aiText = geminiData.output_text;
    }

    if (!aiText && typeof geminiData.text === "string") {
      aiText = geminiData.text;
    }

    aiText = aiText.trim();

    console.log("Gemini AI response:");
    console.log(aiText);

    if (!aiText) {
      return res.status(502).json({
        error: "Gemini returned an empty response.",
      });
    }

    /* -----------------------------------------------------
       CLEAN JSON
    ----------------------------------------------------- */

    let result;

    try {
      // Remove accidental markdown JSON fences.
      let cleaned = aiText
        .replace(/```json/gi, "")
        .replace(/```/g, "")
        .trim();

      // Find JSON object if extra text was returned.
      const firstBrace = cleaned.indexOf("{");
      const lastBrace = cleaned.lastIndexOf("}");

      if (firstBrace !== -1 && lastBrace !== -1) {
        cleaned = cleaned.substring(firstBrace, lastBrace + 1);
      }

      result = JSON.parse(cleaned);
    } catch (error) {
      console.error("AI JSON parsing failed:");
      console.error(aiText);

      return res.status(502).json({
        error: "AI returned invalid disease analysis data.",
        raw: aiText,
      });
    }

    /* -----------------------------------------------------
       NORMALIZE RESULT
    ----------------------------------------------------- */

    let confidence = Number(result.confidence);

    if (Number.isNaN(confidence)) {
      confidence = 0;
    }

    // If AI returns 0.98 instead of 98.
    if (confidence <= 1) {
      confidence = confidence * 100;
    }

    confidence = Math.max(0, Math.min(100, confidence));

    const finalResult = {
      crop: String(result.crop || "Unknown"),

      disease: String(result.disease || "Unable to analyze"),

      confidence: Number(confidence.toFixed(1)),

      symptoms: String(
        result.symptoms || "No information available."
      ),

      causes: String(
        result.causes || "No information available."
      ),

      treatment: String(
        result.treatment || "No information available."
      ),

      prevention: String(
        result.prevention || "No information available."
      ),
    };

    /* -----------------------------------------------------
       SUCCESS
    ----------------------------------------------------- */

    console.log("");
    console.log("==============================================");
    console.log("DISEASE AI SUCCESS");
    console.log("==============================================");
    console.log("CROP:", finalResult.crop);
    console.log("DISEASE:", finalResult.disease);
    console.log("CONFIDENCE:", finalResult.confidence + "%");
    console.log("==============================================");
    console.log("");

    return res.status(200).json(finalResult);
  } catch (error) {
    /* -----------------------------------------------------
       GENERAL ERROR
    ----------------------------------------------------- */

    console.error("");
    console.error("==============================================");
    console.error("DISEASE AI ERROR");
    console.error("==============================================");
    console.error(error);
    console.error("==============================================");
    console.error("");

    return res.status(500).json({
      error: "Disease detection failed.",
      details: error.message,
    });
  }
});

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

app.listen(PORT, "0.0.0.0", () => {
  console.log("");
  console.log("==============================================");
  console.log("       SMART FARM DISEASE AI SERVER");
  console.log("==============================================");
  console.log("Server running on port:", PORT);
  console.log("Endpoint: POST /analyze-disease");
  console.log("Health: GET /health");
  console.log("Model:", GEMINI_MODEL);
  console.log("Gemini Cloud: CONNECTED CONFIGURATION");
  console.log("==============================================");
  console.log("");
});
