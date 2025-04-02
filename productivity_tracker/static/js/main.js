// JavaScript for the Productivity Tracker application

// Initialize Bootstrap tooltips
document.addEventListener('DOMContentLoaded', function() {
    var tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'))
    var tooltipList = tooltipTriggerList.map(function (tooltipTriggerEl) {
        return new bootstrap.Tooltip(tooltipTriggerEl)
    });

    // Task completion confirmation
    const completeTaskForms = document.querySelectorAll('.complete-task-form');
    completeTaskForms.forEach(form => {
        form.addEventListener('submit', function(event) {
            if (!confirm('Mark this task as completed?')) {
                event.preventDefault();
            }
        });
    });

    // Form validation for task duration
    const taskForms = document.querySelectorAll('.task-form');
    taskForms.forEach(form => {
        form.addEventListener('submit', function(event) {
            const durationField = this.querySelector('[name="duration"]');
            if (durationField && parseInt(durationField.value) <= 0) {
                event.preventDefault();
                alert('Please select a valid duration.');
            }
        });
    });

    // Date field auto-population
    const dateFields = document.querySelectorAll('.date-field-today');
    const today = new Date().toISOString().split('T')[0];
    dateFields.forEach(field => {
        if (!field.value) {
            field.value = today;
        }
    });

    // Task filter functionality
    const taskFilterButtons = document.querySelectorAll('.task-filter-btn');
    const taskItems = document.querySelectorAll('.task-list-item');
    
    taskFilterButtons.forEach(button => {
        button.addEventListener('click', function() {
            // Update active button
            taskFilterButtons.forEach(btn => btn.classList.remove('active'));
            this.classList.add('active');
            
            const filterValue = this.getAttribute('data-filter');
            
            taskItems.forEach(item => {
                if (filterValue === 'all') {
                    item.style.display = '';
                } else if (filterValue === 'completed' && item.classList.contains('task-completed')) {
                    item.style.display = '';
                } else if (filterValue === 'pending' && !item.classList.contains('task-completed')) {
                    item.style.display = '';
                } else {
                    item.style.display = 'none';
                }
            });
        });
    });

    // Confirmation for deleting items
    const deleteButtons = document.querySelectorAll('.delete-btn');
    deleteButtons.forEach(button => {
        button.addEventListener('click', function(event) {
            if (!confirm('Are you sure you want to delete this item? This action cannot be undone.')) {
                event.preventDefault();
            }
        });
    });

    // Confirmation for using nidge card
    const nidgeCardButtons = document.querySelectorAll('.nidge-card-btn');
    nidgeCardButtons.forEach(button => {
        button.addEventListener('click', function(event) {
            if (!confirm('Are you sure you want to use your Nidge Card? You only get one per week.')) {
                event.preventDefault();
            }
        });
    });
});

// Function to update the progress bar dynamically (for step counts)
function updateStepProgress(steps, goal) {
    const progressBar = document.querySelector('.step-progress-bar');
    const progressText = document.querySelector('.step-progress-text');
    
    if (progressBar && progressText) {
        const percentage = Math.min((steps / goal) * 100, 100);
        progressBar.style.width = percentage + '%';
        progressBar.setAttribute('aria-valuenow', steps);
        progressText.textContent = percentage.toFixed(0) + '%';
    }
}

// Function to show flash messages
function showFlashMessage(message, type = 'success') {
    const flashContainer = document.getElementById('flash-messages');
    if (flashContainer) {
        const alertDiv = document.createElement('div');
        alertDiv.className = `alert alert-${type} alert-dismissible fade show`;
        alertDiv.role = 'alert';
        
        alertDiv.innerHTML = `
            ${message}
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        `;
        
        flashContainer.appendChild(alertDiv);
        
        // Auto-dismiss after 5 seconds
        setTimeout(() => {
            alertDiv.classList.remove('show');
            setTimeout(() => alertDiv.remove(), 300);
        }, 5000);
    }
}