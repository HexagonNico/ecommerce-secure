function receiveMessages() {
    fetch('/chatServlet', {
        method: 'GET'
    })
    .then(response => {
        if (!response.ok) { 
            throw new Error(response.statusText);
        }
        return response.json();
    })
    .then(messages => {
        console.log(`Messages received: ${JSON.stringify(messages)}`);
        const messagesContainer = document.querySelector('#messages');
        messagesContainer.innerHTML = '';

        for (let message of messages) {
            let messageElement = document.createElement('div');
            messageElement.textContent = message.content;

            // Add CSS class based on message type
            if (message.sender === 'user') {
                messageElement.classList.add('user-message');
            } else if (message.sender === 'vendor') {
                messageElement.classList.add('vendor-message');
            }

            messagesContainer.appendChild(messageElement);
        }
    })
    .catch(err => console.error(err));
}

// Call receiveMessages() every second
setInterval(receiveMessages, 1000);
