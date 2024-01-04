package com.project.controllers;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.project.dao.ContactRepo;
import com.project.dao.userRepository;
import com.project.entities.Contact;
import com.project.entities.User;
import com.project.helper.message;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;

@Controller
@RequestMapping("/user")
public class UserController {
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@Autowired
	private userRepository dao;
	
	@Autowired
	private ContactRepo cdao;
	
	@ModelAttribute
	public void addCommonData(Model m, Principal p)
	{
		String username=p.getName();
		User user=dao.getUserByUserName(username);
		m.addAttribute("user",user);
		
	}
	@GetMapping("/about")
	public String userabout()
	{
		return "normal/about-user";
	}
	
	@GetMapping("/index")
	public String dashboard(Model m,Principal p)
	{
		System.out.println("This is dashboard");
		String userName = p.getName();
		User user = dao.getUserByUserName(userName);
		m.addAttribute("user",user);
		
		m.addAttribute("title","Smart Contact-"+user.getName());
		return "normal/dash";
	}
	
	//add contact handler
	@GetMapping("/addContactForm")
	public String openAddContactForm(Model m)
	{
		m.addAttribute("title","Smart Contact- Add Contact");
		
		
		return "normal/addcontact";
	}
	
	@PostMapping("/addcontact")
	public String saveContact(@Valid @ModelAttribute("contact") Contact contact,BindingResult res,Model m,@RequestParam("profileimage")MultipartFile file, Principal p,HttpSession session)
	{
		m.addAttribute("contact",new Contact());
		try {
			
			if(file.isEmpty())
			{
				contact.setImage("contact.png");
			}
			else
			{
				contact.setImage(file.getOriginalFilename());
				File savedfile = new ClassPathResource("static/image").getFile();
				Files.copy(file.getInputStream(), Path.of(savedfile.getAbsolutePath()+File.separator+file.getOriginalFilename()), StandardCopyOption.REPLACE_EXISTING);
				
			}
			if(res.hasErrors())
			{
				return "normal/addcontact";
			}
			
			String name = p.getName();
			User user = dao.getUserByUserName(name);
			contact.setUser(user);
			user.getContacts().add(contact);
			session.setAttribute("message",new message("Successfully added Contact!!","success" ));
			
			dao.save(user);
			
		} catch (Exception e) {
			e.printStackTrace();
			session.setAttribute("message",new message("Problem occured while saving!!","danger" ));
		}
//		System.out.println(user);
		
		return "normal/addcontact";
	}
	
	//show contacts
	@GetMapping("/showcontacts/{page}")
	public String Showcontacts(@PathVariable("page") int page,Model m,Principal p)
	{
		String username = p.getName();
		User user = dao.getUserByUserName(username);
		m.addAttribute("title","Contacts");
		
		
		Pageable pageable = PageRequest.of(page,5);//this means 8 records per page
		Page<Contact> contacts = this.cdao.findContactsByUser(user.getId(),pageable);
		m.addAttribute("contacts",contacts);
		m.addAttribute("page",page);
		m.addAttribute("totalpages",contacts.getTotalPages());
		return "normal/contacts";
	}
	
	//showing contact's details
	@GetMapping("/{cId}/contact")
	public String showdetails(@PathVariable("cId") int cId,Model m,Principal p)
	{
		Contact contact = cdao.findById(cId).get();
		String name = p.getName();
		User user = dao.getUserByUserName(name);
		//to make sure --> jo user hai wo sirf apna hi contacts dekhe..kisi aur user ke contact nhi dekh paaye
		if(user.getId()==contact.getUser().getId())
		{
			m.addAttribute("title",contact.getcName());
			m.addAttribute("contact",contact);
		}
		
		return "normal/contact_details";
	}
	
	//delete contact handler
	@GetMapping("/delete/{cid}")
	public String delete(@PathVariable("cid") int cid, Model m,Principal p,HttpSession session)
	{
		String name = p.getName();
		User user = dao.getUserByUserName(name);
		Contact contact = cdao.findById(cid).get();
		
		//to make sure jo user hai wo apna hi contact delete kare...
		if(user.getId()==contact.getUser().getId())
		{
			cdao.delete(contact);
			session.setAttribute("message", new message("Contact Deleted Successfully", "success"));
		}
		
		return "redirect:/user/showcontacts/0";
	}
	
	//open update form handler
	
	@PostMapping("/updateform/{cid}")
	public String updateForm(@PathVariable("cid") int cid,Model m)
	{
		m.addAttribute("title","Update Contact Details");
		Contact contact = cdao.findById(cid).get();
		m.addAttribute("contact",contact);
		return "normal/updateform";
	}
	
	//update form
	@PostMapping("/updatecontact")
	public String updateContact( @ModelAttribute Contact contact, @RequestParam("profileimage")MultipartFile image,Model m, HttpSession session,Principal p){
		try {
			if(image.isEmpty())
			{
				contact.setImage("contact.png");
			}
			
			else
				{
					contact.setImage(image.getOriginalFilename());
					File savedfile = new ClassPathResource("static/image").getFile();
					Files.copy(image.getInputStream(), Path.of(savedfile.getAbsolutePath()+File.separator+image.getOriginalFilename()), StandardCopyOption.REPLACE_EXISTING);
					
				}
			
			
			
				String name = p.getName();
				User user = dao.getUserByUserName(name);
				contact.setUser(user);
				cdao.save(contact);
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "redirect:/user/showcontacts/0" ;
	}
	
	//your profile
	@GetMapping("/profile")
	public String yourprofile(Model m,Principal p)
	{
		String username = p.getName();
		User user = dao.getUserByUserName(username);
		String name = user.getName();
		m.addAttribute("title",name);
		
		return "normal/profile";
	}
	
	
	@GetMapping("/settings")
	public String openSettings(Model m)
	
	{
		m.addAttribute("title","Settings");
		return "normal/settings";
	}
	@PostMapping("/change_pass")
	public String changePassword(@RequestParam("oldpass")String old,@RequestParam("newpass")String newp,@RequestParam("repeatpass")String rep,Principal p,HttpSession session)
	{
		String name = p.getName();
		User user = dao.getUserByUserName(name);
		if(bCryptPasswordEncoder.matches(old,user.getPassword()))
		{
			newp=bCryptPasswordEncoder.encode(newp);
			if(bCryptPasswordEncoder.matches(rep, newp) && !bCryptPasswordEncoder.matches(rep, user.getPassword()))
			{
				user.setPassword(newp);
				dao.save(user);
				session.setAttribute("message", new message("Successfully Changed", "success"));
			}
			else if(bCryptPasswordEncoder.matches(rep, user.getPassword()))
			{
				session.setAttribute("message", new message("Old password and new password can't be same", "warning"));
				return "redirect:/user/settings";
			}
			else
			{
				session.setAttribute("message", new message("Make sure repeated password and new password are same", "warning"));
				return "redirect:/user/settings";
			}
			
		}
		else
		{
			session.setAttribute("message", new message("Enter Corrrect Password", "danger"));
			return "redirect:/user/settings";
		}
		return "redirect:/user/index";
	}
	
	//payment
	@PostMapping("/premium")
	@ResponseBody
	public String PayForSubscription() throws RazorpayException
	{
		RazorpayClient client = new RazorpayClient("rzp_test_bgKFJsFoxd29mf","yuvOXfrjYFyaJ8q8lsPdGsiu");
		//code taken from razorpay site to create order
		JSONObject orderRequest = new JSONObject();
		orderRequest.put("amount",500*100); //amount taken is in paise
		orderRequest.put("currency","INR");
		orderRequest.put("receipt", "premium-1");

//		creating new order
		Order order=client.orders.create(orderRequest);
		return order.toString();
	}

}
