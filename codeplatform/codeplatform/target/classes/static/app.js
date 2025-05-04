function showSection(sectionId) {
    document.querySelectorAll('.section').forEach(sec => sec.style.display = 'none');
    document.getElementById(sectionId).style.display = 'block';
}
function runCode() {
    const code = document.getElementById('code').value;
    const language = document.getElementById('language').value;

    fetch('/run', {
        method: 'POST',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify({code: code, language: language, input: ''})
    })
        .then(response => response.text())
        .then(data => {
            const outputElement = document.getElementById('output');
            outputElement.className = `language-${language}`; // set class dynamically
            outputElement.textContent = data; // important: use textContent
            Prism.highlightAll(); // call highlighting
        })
        .catch(error => console.error('Error:', error));
}
function reviewCode() {
    const code = document.getElementById('code').value;
    const language = document.getElementById('language').value;

    fetch('/review', {
        method: 'POST',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify({code: code, language: language})
    })
        .then(response => response.text())
        .then(data => {
            // Open review.html and pass the review text as query parameter
            const reviewEncoded = encodeURIComponent(data);
            window.open('review.html?review=' + reviewEncoded, '_blank');
        })
        .catch(error => console.error('Error:', error));
}



function saveCode() {
    const code = document.getElementById('codeArea').value;
    const input = document.getElementById('inputArea').value;

    fetch('/api/code/save', {
        method: 'POST',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify({code, input})
    })
        .then(response => response.text())
        .then(message => {
            alert(message);
        })
        .catch(error => console.error('Error:', error));
}
