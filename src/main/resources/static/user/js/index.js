// src/main/resources/static/user/js/index.js

document.addEventListener('DOMContentLoaded', function() {
    // Make entire movie grid items clickable
    const movieItems = document.querySelectorAll('.homepage-movie-grid .col');

    movieItems.forEach(item => {
        const link = item.querySelector('a');
        if (link) {
            item.addEventListener('click', function() {
                window.location.href = link.getAttribute('href');
            });
        }
    });
});
