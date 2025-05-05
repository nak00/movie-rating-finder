// Movie data storage
let currentMovie = null;
let searchResults = [];
let currentPage = 1;
let totalResults = 0;
let currentSearchTerm = '';

function searchMovies() {
    const title = document.getElementById('movieTitle').value;

    if (!title) {
        showError('Please enter a movie title');
        return;
    }

    currentSearchTerm = title;
    currentPage = 1;

    // Hide previous results/errors
    document.getElementById('searchResultsList').style.display = 'none';
    document.getElementById('movieDetails').style.display = 'none';
    document.getElementById('searchError').style.display = 'none';

    fetchSearchResults(title, currentPage);
}

function fetchSearchResults(title, page) {
    // Make API request for search results
    fetch(`https://www.omdbapi.com/?apikey=${apiKey}&s=${encodeURIComponent(title)}&page=${page}`)
        .then(response => {
            if (!response.ok) {
                throw new Error(`HTTP error! Status: ${response.status}`);
            }
            return response.json();
        })
        .then(data => {
            if (data.Response === "True") {
                // Store search results
                searchResults = data.Search || [];
                totalResults = parseInt(data.totalResults) || 0;

                // Display search results
                displaySearchResults();

                // Show results list
                document.getElementById('searchResultsList').style.display = 'block';
            } else {
                // If no results found, try a direct title search
                fetchMovieByTitle(title);
            }
        })
        .catch(error => {
            console.error('Error:', error);
            showError(`Error: ${error.message}`);
        });
}

function fetchMovieByTitle(title) {
    // Make API request for exact title match
    fetch(`https://www.omdbapi.com/?apikey=${apiKey}&t=${encodeURIComponent(title)}`)
        .then(response => {
            if (!response.ok) {
                throw new Error(`HTTP error! Status: ${response.status}`);
            }
            return response.json();
        })
        .then(data => {
            if (data.Response === "True") {
                // Store and display single movie
                displayMovieDetails(data);
            } else {
                showError(`Movie not found: ${data.Error}`);
            }
        })
        .catch(error => {
            console.error('Error:', error);
            showError(`Error: ${error.message}`);
        });
}

function fetchMovieById(imdbId) {
    // Make API request for movie by ID
    fetch(`https://www.omdbapi.com/?apikey=${apiKey}&i=${encodeURIComponent(imdbId)}`)
        .then(response => {
            if (!response.ok) {
                throw new Error(`HTTP error! Status: ${response.status}`);
            }
            return response.json();
        })
        .then(data => {
            if (data.Response === "True") {
                // Store and display single movie
                displayMovieDetails(data);
            } else {
                showError(`Movie not found: ${data.Error}`);
            }
        })
        .catch(error => {
            console.error('Error:', error);
            showError(`Error: ${error.message}`);
        });
}

function displaySearchResults() {
    const tableBody = document.getElementById('resultsTableBody');
    tableBody.innerHTML = '';

    searchResults.forEach(movie => {
        const row = document.createElement('tr');

        // Poster cell
        const posterCell = document.createElement('td');
        if (movie.Poster && movie.Poster !== "N/A") {
            const img = document.createElement('img');
            img.src = movie.Poster;
            img.alt = movie.Title;
            img.style.height = '50px';
            img.style.width = 'auto';
            img.style.borderRadius = '4px';
            posterCell.appendChild(img);
        } else {
            posterCell.innerHTML = '<span class="badge bg-secondary">No Image</span>';
        }
        row.appendChild(posterCell);

        // Title cell
        const titleCell = document.createElement('td');
        titleCell.textContent = movie.Title;
        row.appendChild(titleCell);

        // Year cell
        const yearCell = document.createElement('td');
        yearCell.textContent = movie.Year;
        row.appendChild(yearCell);

        // Type cell
        const typeCell = document.createElement('td');
        typeCell.textContent = movie.Type.charAt(0).toUpperCase() + movie.Type.slice(1);
        row.appendChild(typeCell);

        // Actions cell
        const actionsCell = document.createElement('td');

        // View button
        const viewButton = document.createElement('button');
        viewButton.className = 'btn btn-sm admin-btn-secondary me-2';
        viewButton.innerHTML = '<i class="bi bi-eye"></i> View';
        viewButton.onclick = function() { fetchMovieById(movie.imdbID); };
        actionsCell.appendChild(viewButton);

        // Quick Import button
        const importButton = document.createElement('button');
        importButton.className = 'btn btn-sm admin-btn-primary';
        importButton.innerHTML = '<i class="bi bi-download"></i> Quick Import';
        importButton.onclick = function() {
            quickImportMovie(movie.imdbID, this, row);
        };
        actionsCell.appendChild(importButton);

        row.appendChild(actionsCell);

        tableBody.appendChild(row);
    });

    // Update pagination
    updatePagination();
}

function updatePagination() {
    const paginationDiv = document.getElementById('pagination');
    paginationDiv.innerHTML = '';

    if (totalResults <= 10) {
        return; // No pagination needed
    }

    const totalPages = Math.ceil(totalResults / 10);

    const ul = document.createElement('ul');
    ul.className = 'pagination';

    // Previous button
    const prevLi = document.createElement('li');
    prevLi.className = 'page-item' + (currentPage === 1 ? ' disabled' : '');
    const prevLink = document.createElement('a');
    prevLink.className = 'page-link';
    prevLink.href = '#';
    prevLink.innerHTML = '&laquo;';
    prevLink.onclick = function(e) {
        e.preventDefault();
        if (currentPage > 1) {
            currentPage--;
            fetchSearchResults(currentSearchTerm, currentPage);
        }
    };
    prevLi.appendChild(prevLink);
    ul.appendChild(prevLi);

    // Page numbers
    const maxPages = 5; // Show at most 5 page numbers
    const startPage = Math.max(1, currentPage - 2);
    const endPage = Math.min(totalPages, startPage + maxPages - 1);

    for (let i = startPage; i <= endPage; i++) {
        const li = document.createElement('li');
        li.className = 'page-item' + (i === currentPage ? ' active' : '');
        const link = document.createElement('a');
        link.className = 'page-link';
        link.href = '#';
        link.textContent = i;
        link.onclick = function(e) {
            e.preventDefault();
            currentPage = i;
            fetchSearchResults(currentSearchTerm, currentPage);
        };
        li.appendChild(link);
        ul.appendChild(li);
    }

    // Next button
    const nextLi = document.createElement('li');
    nextLi.className = 'page-item' + (currentPage === totalPages ? ' disabled' : '');
    const nextLink = document.createElement('a');
    nextLink.className = 'page-link';
    nextLink.href = '#';
    nextLink.innerHTML = '&raquo;';
    nextLink.onclick = function(e) {
        e.preventDefault();
        if (currentPage < totalPages) {
            currentPage++;
            fetchSearchResults(currentSearchTerm, currentPage);
        }
    };
    nextLi.appendChild(nextLink);
    ul.appendChild(nextLi);

    paginationDiv.appendChild(ul);
}

function displayMovieDetails(data) {
    // Store movie data
    currentMovie = {
        name: data.Title,
        year: parseInt(data.Year) || 0,
        imdbRating: parseFloat(data.imdbRating) || 0.0,
        rottenTomatoesRating: Math.round((parseFloat(data.imdbRating) || 0) * 10),
        description: data.Plot,
        posterUrl: data.Poster !== "N/A" ? data.Poster : '',
        imdbId: data.imdbID
    };

    // Display movie details
    document.getElementById('movieName').textContent = data.Title;
    document.getElementById('movieYear').textContent = data.Year;
    document.getElementById('imdbRating').textContent = data.imdbRating;
    document.getElementById('imdbId').textContent = data.imdbID;
    document.getElementById('moviePlot').textContent = data.Plot;

    // Handle poster
    if (data.Poster && data.Poster !== "N/A") {
        document.getElementById('posterImage').src = data.Poster;
        document.getElementById('posterImage').style.display = 'block';
        document.getElementById('noPoster').style.display = 'none';
    } else {
        document.getElementById('posterImage').style.display = 'none';
        document.getElementById('noPoster').style.display = 'block';
    }

    // Hide search results and show movie details
    document.getElementById('searchResultsList').style.display = 'none';
    document.getElementById('movieDetails').style.display = 'block';
}

function backToResults() {
    // Hide movie details and show search results
    document.getElementById('movieDetails').style.display = 'none';
    document.getElementById('searchResultsList').style.display = 'block';
}

function quickImportMovie(imdbId, buttonElement, rowElement) {
    // Disable the button to prevent multiple clicks
    const buttons = document.querySelectorAll('button');
    buttons.forEach(button => button.disabled = true);

    // Fetch full movie details first
    fetch(`https://www.omdbapi.com/?apikey=${apiKey}&i=${encodeURIComponent(imdbId)}`)
        .then(response => {
            if (!response.ok) {
                throw new Error(`HTTP error! Status: ${response.status}`);
            }
            return response.json();
        })
        .then(data => {
            if (data.Response === "True") {
                // Create movie object
                const movie = {
                    name: data.Title,
                    year: parseInt(data.Year) || 0,
                    imdbRating: parseFloat(data.imdbRating) || 0.0,
                    rottenTomatoesRating: Math.round((parseFloat(data.imdbRating) || 0) * 10),
                    description: data.Plot,
                    posterUrl: data.Poster !== "N/A" ? data.Poster : '',
                    imdbId: data.imdbID
                };

                // Import the movie
                return fetch('/admin/api/movies/import', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        'X-CSRF-TOKEN': csrfToken
                    },
                    body: JSON.stringify(movie)
                });
            } else {
                throw new Error(`Movie not found: ${data.Error}`);
            }
        })
        .then(response => {
            if (!response.ok) {
                throw new Error(`HTTP error! Status: ${response.status}`);
            }
            return response.json();
        })
        .then(data => {
            if (data.success) {
                // Show success message
                rowElement.style.backgroundColor = '#d4edda';

                // Re-enable buttons
                buttons.forEach(button => button.disabled = false);

                // Change the import button to a success message
                if (data.alreadyExists) {
                    buttonElement.innerHTML = '<i class="bi bi-check-circle"></i> Already Imported';
                    buttonElement.className = 'btn btn-sm btn-info';
                } else {
                    buttonElement.innerHTML = '<i class="bi bi-check-circle"></i> Imported';
                    buttonElement.className = 'btn btn-sm btn-success';
                }
                buttonElement.disabled = true;
            } else {
                throw new Error(`Import failed: ${data.message}`);
            }
        })
        .catch(error => {
            console.error('Error:', error);

            // Re-enable buttons
            buttons.forEach(button => button.disabled = false);

            // Show error
            showError(`Import error: ${error.message}`);
        });
}

function importMovie() {
    if (!currentMovie) {
        showError('No movie data to import');
        return;
    }

    // Send movie data to server
    fetch('/admin/api/movies/import', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'X-CSRF-TOKEN': csrfToken
        },
        body: JSON.stringify(currentMovie)
    })
        .then(response => {
            if (!response.ok) {
                throw new Error(`HTTP error! Status: ${response.status}`);
            }
            return response.json();
        })
        .then(data => {
            if (data.success) {
                if (data.alreadyExists) {
                    // Show a message that the movie already exists
                    const importButton = document.querySelector('button.admin-btn-primary');
                    importButton.innerHTML = '<i class="bi bi-check-circle"></i> Already Imported';
                    importButton.className = 'btn btn-info';
                    importButton.disabled = true;

                    // Show a message
                    showError('This movie is already in your database.');
                } else {
                    // Redirect to movies list
                    window.location.href = '/admin/movies';
                }
            } else {
                showError(`Import failed: ${data.message}`);
            }
        })
        .catch(error => {
            console.error('Error:', error);
            showError(`Import error: ${error.message}`);
        });
}

function showError(message) {
    const errorElement = document.getElementById('searchError');
    errorElement.textContent = message;
    errorElement.style.display = 'block';
}
