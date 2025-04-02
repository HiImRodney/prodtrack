// Main JavaScript for Productivity Tracker

// Wait for the DOM to be fully loaded
document.addEventListener('DOMContentLoaded', function() {
    // Set up auto-dismissing alerts
    setTimeout(function() {
        const alerts = document.querySelectorAll('.alert');
        alerts.forEach(function(alert) {
            const bsAlert = new bootstrap.Alert(alert);
            bsAlert.close();
        });
    }, 5000);
    
    // Initialize tooltips
    const tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'))
    const tooltipList = tooltipTriggerList.map(function (tooltipTriggerEl) {
        return new bootstrap.Tooltip(tooltipTriggerEl);
    });

    // Task duration selector
    const durationSelectors = document.querySelectorAll('.duration-selector');
    if (durationSelectors.length) {
        durationSelectors.forEach(btn => {
            btn.addEventListener('click', function() {
                // Remove active class from all buttons
                durationSelectors.forEach(b => b.classList.remove('active', 'btn-primary'));
                durationSelectors.forEach(b => b.classList.add('btn-outline-primary'));
                
                // Add active class to clicked button
                this.classList.add('active', 'btn-primary');
                this.classList.remove('btn-outline-primary');
                
                // Set the value in the hidden input
                document.getElementById('duration').value = this.dataset.value;
            });
        });
    }

    // Step count input with increment/decrement
    const stepInput = document.getElementById('step-count-input');
    const incrementStepBtn = document.getElementById('increment-steps');
    const decrementStepBtn = document.getElementById('decrement-steps');
    
    if (stepInput && incrementStepBtn && decrementStepBtn) {
        incrementStepBtn.addEventListener('click', function() {
            stepInput.value = parseInt(stepInput.value || 0) + 1000;
        });
        
        decrementStepBtn.addEventListener('click', function() {
            let newValue = parseInt(stepInput.value || 0) - 1000;
            stepInput.value = newValue > 0 ? newValue : 0;
        });
    }

    // Date picker initialization
    const datepickers = document.querySelectorAll('.datepicker');
    if (datepickers.length) {
        datepickers.forEach(dp => {
            // If you need datepicker functionality, add flatpickr or similar library
            // Placeholder for date picker initialization
        });
    }

    // Confirm delete modal
    const deleteConfirmModal = document.getElementById('deleteConfirmModal');
    if (deleteConfirmModal) {
        deleteConfirmModal.addEventListener('show.bs.modal', function (event) {
            const button = event.relatedTarget;
            const itemType = button.getAttribute('data-item-type');
            const itemId = button.getAttribute('data-item-id');
            const itemName = button.getAttribute('data-item-name');
            
            const modalTitle = deleteConfirmModal.querySelector('.modal-title');
            const modalBodyContent = deleteConfirmModal.querySelector('.modal-body p');
            const confirmBtn = deleteConfirmModal.querySelector('#confirmDelete');
            
            modalTitle.textContent = `Delete ${itemType}`;
            modalBodyContent.textContent = `Are you sure you want to delete "${itemName}"? This action cannot be undone.`;
            
            confirmBtn.setAttribute('data-item-id', itemId);
            confirmBtn.setAttribute('data-item-type', itemType);
            
            confirmBtn.addEventListener('click', function() {
                const form = document.getElementById(`delete-${itemType}-${itemId}`);
                if (form) {
                    form.submit();
                }
            });
        });
    }
});