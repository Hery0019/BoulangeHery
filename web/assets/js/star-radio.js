// Function to update the style based on the selected radio button
const updateSelectedStar = starsRadioGroup => {
    // Remove the 'checked-star' class from all <i> elements
    const allStars = starsRadioGroup.querySelectorAll('i');
    allStars.forEach(star => star.classList.remove('checked-star'));

    // Find the checked radio input within .stars-radio
    const checkedInput = starsRadioGroup.querySelector('input[type=radio]:checked');
    
    // If a checked input is found, add a class to the preceding <i> element
    if (checkedInput) {
        let checkedLabel = checkedInput.parentElement;

        while (checkedLabel) {
            checkedLabel.querySelector('i').classList.add('checked-star');
            checkedLabel = checkedLabel.previousElementSibling;
        }
    }
};

const updateAllStarRadioGroup = () => {
    const radioGroups = document.getElementsByClassName('stars-radio');
    for (let index = 0; index < radioGroups.length; index++) {
        const radioGroup = radioGroups[index];
        updateSelectedStar(radioGroup)
    }
}

window.addEventListener('load', () => {
    // Initially set the style
    updateAllStarRadioGroup();

    // Add event listeners to each radio button to update the style on change
    let radioButtons = document.querySelectorAll('.stars-radio input[type=radio]');
    radioButtons.forEach(radio => {
        radio.addEventListener('change', e => {
            updateSelectedStar(e.target.parentElement.parentElement);
        });
    });
});
