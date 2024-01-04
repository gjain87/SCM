package com.project.dao;


import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.project.entities.Contact;
import com.project.entities.User;

@Repository
public interface ContactRepo extends JpaRepository<Contact, Integer> {
	
	@Query("from Contact as d where d.user.id=:userId")
	public Page<Contact>findContactsByUser(@Param("userId")int userId,Pageable pageable);
	
	public List<Contact>findBycNameContainingAndUser(String name,User user);
	
}
