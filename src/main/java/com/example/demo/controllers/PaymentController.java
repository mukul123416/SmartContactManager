package com.example.demo.controllers;
import com.example.demo.config.AppConfig;
import com.example.demo.entities.MyOrder;
import com.example.demo.entities.User;
import com.example.demo.repositories.MyOrderRepository;
import com.example.demo.repositories.UserRepository;
import com.paytm.pg.merchant.PaytmChecksum;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.net.URL;
import java.security.Principal;
import java.util.Map;
import java.util.Random;

@RestController
public class PaymentController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MyOrderRepository orderRepository;
    Random random = new Random();

    @PostMapping("/razorpay_create_order")
    public String razorPayCreateOrder(@RequestBody Map<String, Object> data, Principal principal) throws RazorpayException {

        int amt = Integer.parseInt(data.get("amount").toString());

        var client=new RazorpayClient("rzp_test_CzxcPqUG4CiCM9","L2JWHQbJWNfeA1WeI7jqKiLI");

        JSONObject ob = new JSONObject();
        ob.put("amount", amt*100);
        ob.put("currency", "INR");
        ob.put("receipt", "txn_234567");

        Order order = client.Orders.create(ob);

        //save the order in database
        MyOrder myOrder = new MyOrder();
        myOrder.setAmount(order.get("amount"));
        myOrder.setOrderId(order.get("id"));
        myOrder.setStatus(order.get("status"));
        myOrder.setPaymentId(null);
        myOrder.setReceipt(order.get("receipt"));
        myOrder.setUser(this.userRepository.getUserByUserName(principal.getName()));
        this.orderRepository.save(myOrder);

        return order.toString();
    }

    @PostMapping("/razorpay_update_order")
    public ResponseEntity<?> razorPayUpdateOrder(@RequestBody Map<String, Object> data){

        MyOrder myorder = this.orderRepository.findByOrderId(data.get("Order_id").toString());

        myorder.setPaymentId(data.get("Payment_id").toString());
        myorder.setStatus(data.get("Status").toString());

        this.orderRepository.save(myorder);

        return ResponseEntity.ok(Map.of("msg","updated"));
    }

    @PostMapping("/paytm_create_order")
    public Map<String, Object> paytmCreateOrder(@RequestBody Map<String, Object> data, Principal principal, Model model)  {

        String name=principal.getName();
        User user=this.userRepository.getUserByUserName(name);
        model.addAttribute("user",user);

        String orderId = "ORDER" + random.nextInt(10000000);

        //param created
        JSONObject paytmParams = new JSONObject();

        //body information
        JSONObject body = new JSONObject();
        body.put("requestType", "Payment");
        body.put("mid", AppConfig.MID);
        body.put("websiteName", AppConfig.WEBSITE);
        body.put("orderId", orderId);
        body.put("callbackUrl", "https://smartcontactmanager-production.up.railway.app/payment-success");

        JSONObject txnAmount = new JSONObject();
        txnAmount.put("value", data.get("amount"));
        txnAmount.put("currency", "INR");

        JSONObject userInfo = new JSONObject();
        userInfo.put("custId", user.getId());

        body.put("txnAmount", txnAmount);
        body.put("userInfo", userInfo);

        String responseData = "";
        ResponseEntity<Map> response = null;

        try {

            String checksum = PaytmChecksum.generateSignature(body.toString(), AppConfig.MKEY);

            JSONObject head = new JSONObject();
            head.put("signature", checksum);

            paytmParams.put("body", body);
            paytmParams.put("head", head);

            /* for Staging */
            URL url = new URL("https://securegw-stage.paytm.in/theia/api/v1/initiateTransaction?mid=" + AppConfig.MID + "&orderId=" + orderId + "");

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> httpEntity = new HttpEntity<>(paytmParams.toMap(), headers);

            //calling api
            RestTemplate restTemplate = new RestTemplate();
            response = restTemplate.postForEntity(url.toString(), httpEntity, Map.class);

        } catch (Exception e) {
            e.printStackTrace();
        }

        Map body1 = response.getBody();
        body1.put("orderId", orderId);
        body1.put("amount", txnAmount.get("value"));
        return body1;
    }
}
