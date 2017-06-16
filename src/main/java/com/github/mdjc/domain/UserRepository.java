package com.github.mdjc.domain;

public interface UserRepository {
	User getByUsername(String username);
}
