function wizardNext(e) {
    if(wiz.getStepIndex(wiz.currentStep) == 3)
        e.stopPropagation();
}

$(document).ready(function() {
    $('#formRegistrar').submit(function() {
        if(wiz.getStepIndex(wiz.currentStep) == 3)
            return true;
        return false;
    });
});