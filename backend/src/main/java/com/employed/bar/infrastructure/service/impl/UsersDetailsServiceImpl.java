package com.employed.bar.infrastructure.service.impl;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.employed.bar.infrastructure.adapter.out.persistence.repository.UserEntityRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UsersDetailsServiceImpl implements UserDetailsService {
	
	private final UserEntityRepository userEntityRepository;
	
	

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		return userEntityRepository.findByEmail(username)
				.orElseThrow(()-> new UsernameNotFoundException("User with email " + username + " not found"));
	}
	

}
