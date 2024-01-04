package com.project.controllers;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.project.dao.userRepository;
import com.project.entities.User;
import com.project.helper.message;




@Controller
public class HomeController {
	@Autowired
	private BCryptPasswordEncoder pass;
//	
	@Autowired
	private userRepository dao;
	
	@GetMapping("/")
	public String welcome()
	{
	
		return "redirect:home";
	}
	
	@GetMapping("/home")
	public String home(Model m)
	{
		m.addAttribute("title","Home-Smart Contact Manager");
		return "home";
	}
	
	@GetMapping("/signup")
	public String signup(Model m)
	{
		m.addAttribute("title","SignUp-Smart Contact Manager");
		m.addAttribute("user",new User());
		return "signup";
	}
	
	@GetMapping("/about")
	public String about(Model m)
	{
		m.addAttribute("title","About-Smart Contact Manager");
		return "about";
	}
	
	//note: binding result must be exactly after modelattribute
	@PostMapping("/register")
	public String registeruser(@Valid @ModelAttribute User user ,BindingResult res, @RequestParam(value = "agreement",defaultValue = "False")boolean agreement, Model m,javax.servlet.http.HttpSession session )
	{
		try {
			if(!agreement)
			{
				throw new Exception("You have not agreed to the terms and conditions.");
			}
			if(res.hasErrors())
			{
				System.out.println(res);
				return "signup";
			}
			user.setEnabled(true);
			user.setRole("ROLE_USER");
			user.setImageUrl("default.png");
			user.setPassword(pass.encode(user.getPassword()));
			this.dao.save(user);
			session.setAttribute("message", new message("Successfully Registered User !!", "alert-success"));
			return "signup";
			
		} catch (Exception e) {
			session.setAttribute("message",new message("Something went wrong !! "+e.getMessage(), "alert-danger"));
			e.printStackTrace();
			return "signup";
			
		}
		
	}

	@RequestMapping(value = "/signin")
	public String loginPage(Model m)
	{
		m.addAttribute("title","Login Page-Smart Contact Manager");
		return "login";
	}
}
