package com.project.controllers;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.project.dao.ContactRepo;
import com.project.dao.userRepository;
import com.project.entities.Contact;
import com.project.entities.User;

@RestController
public class SearchController {
	@Autowired
	private userRepository dao;
	@Autowired
	private ContactRepo cdao;
	
	@GetMapping("/search/{query}")
	public ResponseEntity<?>search(@PathVariable("query")String query, Principal p)
	{
		String userName = p.getName();
		User user = dao.getUserByUserName(userName);
		List<Contact> contact = this.cdao.findBycNameContainingAndUser(query, user);
		return ResponseEntity.ok(contact);
	}

}
