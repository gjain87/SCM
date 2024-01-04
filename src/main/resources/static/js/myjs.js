const toggleSideBar = () => {
    if ($(".sidebar").is(":visible")) {
        $(".sidebar").css("display", "none");
        $(".content").css("margin-left", "2%");
    }
    else {
        $(".sidebar").css("display", "block");
        $(".content").css("margin-left", "20%");
    }
}

const search=()=>
{
    // console.log("Searching");
    let query=$("#search-input").val();
    if(query=="")
    {
        $(".search-result").hide();
    }
    else{
		//sending request to server to search the query
		let url=`http://localhost:8080/search/${query}`;
		fetch(url).then((response)=>{
			return response.json();
		}).then((data)=>{
			
			let text=`<div class='list-group'>`;
			data.forEach((contact)=>{
				text+=`<a href='/user/${contact.cId}/contact' class='list-group-item list-group-action'> ${contact.cName} </a>`
			});
				
			text+=`</div>`;
			$(".search-result").html(text);
			
		});
       $(".search-result").show();
        //console.log(query);
    }
    
    
};


const paymentStart = () => {
	var amount=500;
    /*console.log("payment started");
    
    console.log(amount);
    if (amount == "" || amount == null || amount < 1) {
       
	        Swal.fire({
	  icon: "error",
	  title: "Oops...",
	  text: "Amount must be Greater than 1",
	  
	});
        return;
    }*/

    //we will use ajax to send request to server
    $.ajax(
        {
            url: '/user/premium',
            data: JSON.stringify({ amount: amount, info: "Order_requested" }),
            contentType: 'application/json',
            type: 'POST',
            dataType: 'json',
            success: function (response) {
                if (response.status == "created") {
                    //open payment form
                    let options = {
                        key: 'rzp_test_bgKFJsFoxd29mf',
                        amount: response.amount,
                        currency: 'INR',
                        name: 'Payment Gateway App Donation',
                        description: 'Donation',
                        image: 'ghjbkj',
                        order_id:response.id,
                        "handler":function(response)
                        {
                            console.log(response.razorpay_payment_id)
                            console.log(response.razorpay_order_id)
                            console.log(response.razorpay_signature)
                            console.log('payment successfull')
                            Swal.fire({
                                position: "top",
                                icon: "success",
                                title: "Payment Successful",
                                showConfirmButton: false,
                                timer: 1500
                              });
                        },
                        prefill:{
                            name:"",
                            email:"",
                            contact:"",
                        },
                        theme:{
                            color:"black"
                        },

                    };
                    let rzp=new Razorpay(options);
                    rzp.on('payment.failed', function (response){
                        console.log(response.error.code);
                        console.log(response.error.description);
                        console.log(response.error.source);
                        console.log(response.error.step);
                        console.log(response.error.reason);
                        console.log(response.error.metadata.order_id);
                        console.log(response.error.metadata.payment_id);
                       Swal.fire({
								  icon: "error",
								  title: "OOPS...",
								  text: "Payment failed",
							  
								});
                });
                    rzp.open();
                }
                console.log(response)
            },
            error: function (error) {
                alert("something went wrong")
            }
        }
    )
}













