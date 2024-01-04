package com.project.controllers;

import java.util.Random;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.project.dao.userRepository;
import com.project.entities.User;
import com.project.helper.message;
import com.project.services.EmailSender;
@Controller
public class ForgotController {
	
	@Autowired
	private BCryptPasswordEncoder encoder;
	
	@Autowired
	private EmailSender emailsender;
	
	@Autowired
	private userRepository udao;
	
	Random random = new Random(1000);//minimum value will be 1000
	
	@RequestMapping("/forgot")
	public String Forgetpasswordform(Model m)
	{
		m.addAttribute("title","Forgot Password");
		return "forgetpasswordform";
	}
	
	@PostMapping("/forgotpassword")
	public String SendOtp(@RequestParam("registeredemail")String email,HttpSession session,Model m)
	{
		//generating OTP
		
		int otp = random.nextInt(999999);//maximum value will be 9999
//		System.out.println(otp);
//		System.out.println(random);
		System.out.println(email);
		
		//send OTP to email
		String subject="OTP Verification";
		String message="OTP: "+otp;
		boolean sendemail = this.emailsender.sendemail(message,subject, email);
		
		if(sendemail)
		{
			session.setAttribute("email", email);
			session.setAttribute("sentotp", otp);
			session.setAttribute("message", new message("OTP has been sent to your email....", "success"));
			return "verify_otp";
		}
		else
		{
			session.setAttribute("message", new com.project.helper.message("Invalid Email...", "danger"));
			return "forgetpasswordform";
		}
		
		
	}
	
	@PostMapping("/verify_otp")
	public String verifyotp(@RequestParam("otp")int otp,HttpSession session,Model m)
	{
		m.addAttribute("title","Change Password");
		int sentotp=(int) session.getAttribute("sentotp");
		String useremail=(String) session.getAttribute("email");
		if(otp==sentotp)
		{
			User user = udao.getUserByUserName(useremail);
			if(user==null) {
				//send error message
				session.setAttribute("message", new message("You are not Registered!! Please Register..", "danger"));
				return "forgetpasswordform";
			}
			else {
				
			}
			return "changepasswordform";
		}
		else
		{
			session.setAttribute("message", new message("Wrong OTP!!", "warning"));
			return "verify_otp";
		}
	}
	
	@PostMapping("/changepwd")
	public String changepwd(@RequestParam("newpass")String newp,@RequestParam("reppass")String repp,Model m,HttpSession session)
	{
		m.addAttribute("title","Password Changed");
		String useremail = (String) session.getAttribute("email");
		User user = udao.getUserByUserName(useremail);
//		System.out.println("newpp: "+newp+" repp: "+repp);
		newp=encoder.encode(newp);
		if(encoder.matches(repp, newp))
		{
			
			user.setPassword(newp);
			udao.save(user);
			session.setAttribute("message", new message("Password Changed Successfully....", "success"));
			return "login";
		}
		else {
		session.setAttribute("message", new message("Password didnot match. Please give correct password...", "warning"));
		return "changepasswordform";
		}
		
	}

}
