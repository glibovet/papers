function showErrorMessage(error) {
    if (typeof error === 'string') {
        alertify.error(error);
    } else {
        alertify.error(error.message);
    }
}

function showSuccessMessage(message) {
    alertify.success(message);
}

function showInfoMessage(message) {
    alertify.message(message);
}

function buildValidationErrors(errors) {
    if (!errors || errors.length < 1) {
        return '';
    }

    return '<br />[' + errors.join(', ') + ']';
}