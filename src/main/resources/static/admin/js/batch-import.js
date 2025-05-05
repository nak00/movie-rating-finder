// Poll for status updates
let statusInterval;

function updateStatus() {
    fetch('/admin/batch-import/status', {
        headers: {
            [csrfHeader]: csrfToken
        }
    })
        .then(response => response.json())
        .then(data => {
            // Update progress bar
            const progressBar = document.querySelector('.progress-bar');
            const percentage = data.progressPercentage;
            progressBar.style.width = percentage + '%';
            progressBar.setAttribute('aria-valuenow', percentage);
            progressBar.textContent = percentage + '%';

            // Update counters
            document.getElementById('totalMovies').textContent = data.totalMovies;
            document.getElementById('importedMovies').textContent = data.imported;
            document.getElementById('duplicateMovies').textContent = data.duplicates;
            document.getElementById('errorMovies').textContent = data.errors;

            // Update status message
            document.getElementById('statusMessage').textContent = data.status;

            // Show/hide error message
            if (data.lastError && data.lastError.length > 0) {
                document.getElementById('errorMessage').textContent = data.lastError;
                document.getElementById('errorAlert').style.display = 'block';
            } else {
                document.getElementById('errorAlert').style.display = 'none';
            }

            // Enable/disable form based on import status
            const form = document.getElementById('batchImportForm');
            const startBtn = document.getElementById('startImportBtn');

            if (data.inProgress) {
                form.querySelectorAll('input').forEach(input => input.disabled = true);
                startBtn.disabled = true;
                startBtn.textContent = 'Import in Progress...';
            } else {
                form.querySelectorAll('input').forEach(input => input.disabled = false);
                startBtn.disabled = false;
                startBtn.textContent = 'Start Import';
            }

            // Stop polling if import is complete
            if (!data.inProgress && statusInterval) {
                clearInterval(statusInterval);
                statusInterval = null;
            }
        })
        .catch(error => {
            console.error('Error fetching status:', error);
        });
}

// Start polling when form is submitted
document.addEventListener('DOMContentLoaded', function() {
    document.getElementById('batchImportForm').addEventListener('submit', function() {
        // Start polling for status updates
        if (!statusInterval) {
            statusInterval = setInterval(updateStatus, 1000);
        }
    });

    // Initial status update
    updateStatus();
});
