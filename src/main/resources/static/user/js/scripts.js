// Fade-in effect for movie detail page
document.addEventListener('DOMContentLoaded', function() {
    var container = document.querySelector('.movie-detail-container');
    if (container) {
        container.style.opacity = 0;
        setTimeout(function() {
            container.style.transition = 'opacity 0.7s';
            container.style.opacity = 1;
        }, 50);
    }
});
