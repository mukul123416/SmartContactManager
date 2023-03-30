const toggleSidebar = () => {
   if($(".sidebar").is(":visible")){
       $(".sidebar").css("display","none");
       $(".content").css("margin-left","0%");
    } else {
      $(".sidebar").css("display","block");
      $(".content").css("margin-left","20%");
    }
   };



   const search = () => {

     let query = $("#search-input").val();

     if(query == ""){
       $(".search-result").hide();
     }
     else{

       let url = `https://smartcontactmanager-production.up.railway.app/search/${query}`;


       fetch(url)
         .then((response) => {
           return response.json();
         })
         .then((data) => {
            let text = `<div class='list-group'>`;
            data.forEach((contact) => {
              text += `<a href='/user/contact/${contact.cid}' class='list-group-item list-group-item-action'> ${contact.name} </a>`
            });
            text += `</div>`;
            $(".search-result").html(text);
            $(".search-result").show();
         });

     }
   };


//first request to server to create order
const razorPayPaymentStart = () =>{
  const regex = new RegExp(/^(?:0|[1-9]\d*)(?:\.(?!.*000)\d+)?$/);
  var amount = $("#razorpay_payment_field").val();
  if(amount == "" || amount == null){
    swal("Failed!", "amount is required !!", "error");
    return;
  }else if(!(regex.test(amount))){
     swal("Failed!", "Invalid amount !!", "error");
     return;
  }

  $.ajax({
    url:'/razorpay_create_order',
    data:JSON.stringify({amount:amount, info:"order_request"}),
    contentType:'application/json',
    type:'POST',
    dataType:'json',
    success:function(response){
      if(response.status == 'created'){

        let options={
          key: "rzp_test_CzxcPqUG4CiCM9",
          amount: "response.amount",
          currency: "INR",
          name: "Smart Contact Manager",
          description: "Donation",
          image: "https://storage.googleapis.com/proudcity/sanrafaelca/uploads/2020/04/donate-image.png",
          order_id:response.id,
          handler: function (response) {
            updateRazorPayPaymentOnServer(response.razorpay_payment_id,response.razorpay_order_id,"Paid");
          },
          prefill: {
            name: "",
            email: "",
            contact: ""
          },
          notes: {
            address: "Razorpay Corporate Office"
          },
          theme: {
            color: "#3399cc"
          }
        };

        let rzp=new Razorpay(options);
        rzp.open();
        rzp.on('payment.failed', function (response){
          console.log(response.error.code);
          console.log(response.error.description);
          console.log(response.error.source);
          console.log(response.error.step);
          console.log(response.error.reason);
          console.log(response.error.metadata.order_id);
          console.log(response.error.metadata.payment_id);
          swal("Failed!", "Oops payment failed !!", "error");
       });

      }
    },
    error:function(error){
      console.log(error);
      swal("Failed !!", "something went wrong !!", "error");
    }
  })
};


function updateRazorPayPaymentOnServer(Payment_id,Order_id,Status)
{
  $.ajax({
    url:'/razorpay_update_order',
    data:JSON.stringify({Payment_id:Payment_id, Order_id:Order_id,Status:Status}),
    contentType:'application/json',
    type:'POST',
    dataType:'json',
    success:function(response){
      swal("Good job!", "congrats !! Payment successful !!", "success");
      $("#razorpay_payment_field").val()=='';
    },
    error:function(error){
      swal("Failed !!", "Your payment is successful , but we did't get on server , we will contact you as soon as possible", "error");
      $("#razorpay_payment_field").val()=='';
    }
  });
}


 //start paytm payment function
    async function paytmPaymentStart() {
        const regex = new RegExp(/^(?:0|[1-9]\d*)(?:\.(?!.*000)\d+)?$/);
        const amount = document.querySelector("#paytm_payment_field").value;
         if(amount == "" || amount == null){
            swal("Failed!", "amount is required !!", "error");
            return;
          }else if(!(regex.test(amount))){
                swal("Failed!", "Invalid amount !!", "error");
                return;
             }
         $.ajax({
            url:'/paytm_create_order',
            data:JSON.stringify({amount:amount, info:"order_request"}),
            contentType:'application/json',
            type:'POST',
            dataType:'json',
            success:function(response){
             initiateClientModule(response);
            },
            error:function(error){
              swal("Failed !!", "something went wrong !!", "error");
            }
          })
    };



 function initiateClientModule(data) {
        const script = document.createElement("script");
        script.src = ` https://securegw-stage.paytm.in/merchantpgpui/checkoutjs/merchants/RHLBnF07039627707073.js`;
        script.crossOrigin = `anonymous`;
        script.onload = () => {

            var config = {
                "root": "",
                "flow": "DEFAULT",
                "data": {
                    "orderId": data.orderId, /* update order id */
                    "token": data.body.txnToken, /* update token value */
                    "tokenType": "TXN_TOKEN",
                    "amount": data.amount/* update amount */
                         },
                 "merchant": {
                          mid: "RHLBnF070327707073",
                          redirect: false
                             },

                            "handler": {
                                "notifyMerchant": function (eventName, data) {
                                    console.log("notifyMerchant handler function called");
                                    console.log("eventName => ", eventName);
                                    console.log("data => ", data);
                                },
                                "transactionStatus": function (data) {
                                    console.log("transaction completed")
                                    console.log(data)
                                   if (data.STATUS = "TXN_FAILURE") {
                                     alert(data.RESPMSG)
                                 } else if (data.STATUS == 'TXN_SUCCESS') {
                                     alert(data.RESPMSG)

                                     //capture api with data : call kar lo
                                 } else {
                                     alert("Something wend wrong while payment contact to admin!!")
                                 }

                                 window.Paytm.CheckoutJS.close();

                             }
                         }
                     };
         if (window.Paytm && window.Paytm.CheckoutJS) {
                    window.Paytm.CheckoutJS.onLoad(function excecuteAfterCompleteLoad() {
                        // initialze configuration using init method
                        window.Paytm.CheckoutJS.init(config).then(function onSuccess() {
                            // after successfully updating configuration, invoke JS Checkout
                            window.Paytm.CheckoutJS.invoke();
                        }).catch(function onError(error) {
                            console.log("error => ", error);
                        });
                    });
                }

            }
            document.body.appendChild(script);
       }


function myFunction() {
  var x = document.getElementById("password");
  if (x.type === "password") {
    x.type = "text";
  } else {
    x.type = "password";
  }
}

function myFunction1() {
  var x = document.getElementById("oldPassword");
  var y = document.getElementById("newPassword");
  if (x.type === "password" || y.type === "password" ) {
    x.type = "text";
    y.type = "text";
   } else {
    x.type = "password";
    y.type = "password";
  }
}

function myFunction2() {
  var x = document.getElementById("password_field");
  if (x.type === "password") {
    x.type = "text";
  } else {
    x.type = "password";
  }
}

function myFunction3() {
  var x = document.getElementById("id_password");
  if (x.type === "password") {
    x.type = "text";
  } else {
    x.type = "password";
  }
}

