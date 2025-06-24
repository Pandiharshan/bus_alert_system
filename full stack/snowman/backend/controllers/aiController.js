// backend/controllers/aiController.js

const generateImage = async (req, res) => {
  const { prompt } = req.body;
  if (!prompt) {
    return res.status(400).json({ error: 'Prompt is required.' });
  }
  // Mock image generation (replace with real API call later)
  const imageUrl = `https://placehold.co/512x512?text=${encodeURIComponent(prompt)}`;
  res.json({ imageUrl });
};

module.exports = { generateImage }; 