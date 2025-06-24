const express = require('express');
const router = express.Router();
const { generateImage } = require('../controllers/aiController');

router.post('/generate', generateImage);
 
module.exports = router; 