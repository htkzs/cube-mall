
package com.kkb.service;

import javax.jws.WebService;

@WebService
public interface UserService {
    public String getUser(Long userId);

}