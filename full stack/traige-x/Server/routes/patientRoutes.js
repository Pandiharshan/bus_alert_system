import express from 'express';
import { 
  createPatient, 
  getPatients, 
  getPatientById, 
  updatePatient, 
  deletePatient 
} from '../controllers/patientController.js';
import { protect } from '../middleware/authMiddleware.js';

const router = express.Router();

// All routes are protected - require authentication
router.use(protect);

// @route   POST /api/patients
// @desc    Create a new patient
// @access  Private
router.post('/', createPatient);

// @route   GET /api/patients
// @desc    Get all patients for the authenticated user
// @access  Private
router.get('/', getPatients);

// @route   GET /api/patients/:id
// @desc    Get patient by ID
// @access  Private
router.get('/:id', getPatientById);

// @route   PUT /api/patients/:id
// @desc    Update patient
// @access  Private
router.put('/:id', updatePatient);

// @route   DELETE /api/patients/:id
// @desc    Delete patient
// @access  Private
router.delete('/:id', deletePatient);

export default router;