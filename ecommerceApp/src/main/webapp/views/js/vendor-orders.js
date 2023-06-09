window.onload = function() {
    fetch('/getOrdersServlet')
        .then(response => {
            if (!response.ok) { 
                throw new Error(response.statusText);
            }
            return response.json();
        })
        .then(data => {
            let ordersMap = {};

            for (let item of data) {
                if (!ordersMap[item.id]) {
                    ordersMap[item.id] = {
                        id: item.id,
                        orderDate: item.orderDate,
                        status: item.status,
                        items: []
                    };
                }

                ordersMap[item.id].items.push({
                    productName: item.productName,
                    quantity: item.quantity,
                    price: item.price,
                    review: item.review,
                    rating: item.rating,
                    reply: item.reply,
                    orderItemId: item.orderItemId
                });
            }

            let ordersDiv = document.querySelector('.orders');

            for (let id in ordersMap) {
                let orderCard = document.createElement('div');
                orderCard.className = 'order-card';
                
                
                let itemsHtml = ordersMap[id].items.map(item => `
                <div class="order-item-product">
                    <p>Product Name: ${item.productName}</p>
                    <p>Quantity: ${item.quantity}</p>
                    <p>Price: ${item.price}</p>
                    ${item.review ? `<p class="review">Review: ${item.review}</p><p class="rating">Rating: ${item.rating}</p>${item.reply ? `<p class="reply">Reply: ${item.reply}</p>` : 
                        `<form class="reply-form" onsubmit="submitReply(event, ${item.orderItemId})">
                            <textarea name="replyText" placeholder="Write your reply here"></textarea>
                            <button type="submit">Submit Reply</button>
                        </form>
                    `}` : ''}
                </div>
            `).join('');


                orderCard.innerHTML = `
                <h3>Order ID: ${ordersMap[id].id}</h3>
                ${itemsHtml}
                <p>Status: ${ordersMap[id].status}</p>
                <p>Order Date: ${new Date(ordersMap[id].orderDate).toLocaleDateString()}</p>
                <button onclick="approveOrder(${ordersMap[id].id})">Approve</button>
                `;


                ordersDiv.appendChild(orderCard);
            }
        })
        .catch(error => console.error(error));
};

function submitReply(event, orderItemId) {
  event.preventDefault();

  let replyText = event.target.elements.namedItem("replyText").value;

  let formdata = new URLSearchParams();
  formdata.append("orderItemId", orderItemId);
  formdata.append("replyText", replyText);

  fetch("/replyServlet", {
    method: "POST",
    body: formdata,
  })
    .then((response) => {
      if (!response.ok) {
        throw new Error(response.statusText);
      }
      alert("Reply Submitted");

      event.target.innerHTML = `
        <p><strong>Reply Submitted:</strong> ${replyText}</p>
    `;
    })
    .catch((err) => console.error(err));
}

function approveOrder(orderId) {
    let formdata = new URLSearchParams();
    formdata.append("orderId", orderId);

    fetch("/VendorServlet", {
        method: "POST",
        body: formdata,
    })
        .then((response) => {
            if (!response.ok) {
                throw new Error(response.statusText);
            }
            alert("Order approved");
            // Here you might want to update the status of the order in the UI.
        })
        .catch((err) => console.error(err));
}
