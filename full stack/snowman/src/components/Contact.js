import React, { useState } from 'react';
import './Contact.css';

const Contact = () => {
  const [formData, setFormData] = useState({
    name: '',
    email: '',
    message: ''
  });

  const [feedback, setFeedback] = useState("");

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    // Handle form submission here
    console.log('Form submitted:', formData);
    setFeedback("Thank you! Your message has been sent.");
    // Reset form after submission
    setFormData({
      name: '',
      email: '',
      message: ''
    });
  };

  return (
    <section id="contact" className="contact">
      <div className="contact-container flow-border">
        <div className="contact-header">
          <h2 className="contact-title glow-underline-hover">Get in Touch</h2>
          <p className="contact-subtitle">
            Ready to start your next project? Let's create something amazing together.
          </p>
        </div>

        <div className="contact-form-wrapper">
          <form className="contact-form" onSubmit={handleSubmit}>
            <div className="form-group">
              <label htmlFor="name" className="form-label">
                Name
              </label>
              <input
                type="text"
                id="name"
                name="name"
                value={formData.name}
                onChange={handleChange}
                className="form-input"
                placeholder="Your full name"
                required
                aria-describedby="name-desc"
              />
              <span id="name-desc" className="visually-hidden">Enter your full name</span>
            </div>

            <div className="form-group">
              <label htmlFor="email" className="form-label">
                Email
              </label>
              <input
                type="email"
                id="email"
                name="email"
                value={formData.email}
                onChange={handleChange}
                className="form-input"
                placeholder="your.email@example.com"
                required
                aria-describedby="email-desc"
              />
              <span id="email-desc" className="visually-hidden">Enter your email address</span>
            </div>

            <div className="form-group">
              <label htmlFor="message" className="form-label">
                Message
              </label>
              <textarea
                id="message"
                name="message"
                value={formData.message}
                onChange={handleChange}
                className="form-textarea"
                placeholder="Tell us about your project..."
                rows="5"
                required
                aria-describedby="message-desc"
              />
              <span id="message-desc" className="visually-hidden">Describe your project or inquiry</span>
            </div>

            <button type="submit" className="form-submit">
              Send Message
            </button>
            <div aria-live="polite" className="form-feedback">{feedback}</div>
          </form>
        </div>
      </div>
    </section>
  );
};

export default Contact;