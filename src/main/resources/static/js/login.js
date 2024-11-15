
function toggleForms() {
    const formContainer = document.querySelector('.form-container');
  
    formContainer.style.display = formContainer.style.display === 'none' || formContainer.style.display === '' ? 'flex' : 'none';
}

function toggleForm(formType) {
    const loginForm = document.getElementById('login-form');
    const registerForm = document.getElementById('register-form');

    if (formType === 'login') {
        loginForm.style.display = 'block';
        registerForm.style.display = 'none';
    } else {
        loginForm.style.display = 'none';
        registerForm.style.display = 'block';
    }
}


document.getElementById('login').addEventListener('submit', function(event) {
    event.preventDefault();
    alert('Login successful!');
    window.location.href = '/';  
});

document.getElementById('register').addEventListener('submit', function(event) {
    event.preventDefault();
    alert('Registration successful!');
    window.location.href = '/';  
});

