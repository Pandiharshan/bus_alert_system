// Make navbar background solid when scrolling
const navbar = document.querySelector('.navbar');

window.addEventListener('scroll', () => {
  if (window.scrollY > 50) {
    navbar.classList.add('navbar-scrolled');
  } else {
    navbar.classList.remove('navbar-scrolled');
  }
});

// Smooth scrolling for navigation links
document.querySelectorAll('a[href^="#"]').forEach(anchor => {
  anchor.addEventListener('click', function (e) {
    e.preventDefault();
    const target = document.querySelector(this.getAttribute('href'));
    if (target) {
      window.scrollTo({
        top: target.offsetTop - 70, // Adjust for fixed navbar
        behavior: 'smooth'
      });
    }
  });
});

// Initialize tooltips
var tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
var tooltipList = tooltipTriggerList.map(function (tooltipTriggerEl) {
  return new bootstrap.Tooltip(tooltipTriggerEl);
});

// Add animation to features on scroll
const animateOnScroll = () => {
  const features = document.querySelectorAll('.feature-box');
  
  features.forEach((feature, index) => {
    const featurePosition = feature.getBoundingClientRect().top;
    const screenPosition = window.innerHeight / 1.3;
    
    if (featurePosition < screenPosition) {
      feature.style.opacity = '1';
      feature.style.transform = 'translateY(0)';
    }
  });
};

// Set initial styles for animation
window.addEventListener('load', () => {
  const features = document.querySelectorAll('.feature-box');
  features.forEach((feature, index) => {
    feature.style.transition = `all 0.5s ease ${index * 0.2}s`;
    feature.style.opacity = '0';
    feature.style.transform = 'translateY(20px)';
  });
  
  // Trigger initial check
  animateOnScroll();
});

// Add scroll event listener
window.addEventListener('scroll', animateOnScroll);

// Add animation to pricing cards on hover
const pricingCards = document.querySelectorAll('.card');

pricingCards.forEach(card => {
  card.addEventListener('mouseenter', () => {
    card.style.transform = 'translateY(-10px)';
    card.style.boxShadow = '0 10px 20px rgba(0,0,0,0.1)';
    card.style.transition = 'all 0.3s ease';
  });
  
  card.addEventListener('mouseleave', () => {
    card.style.transform = 'translateY(0)';
    card.style.boxShadow = '0 2px 10px rgba(0,0,0,0.1)';
  });
});
