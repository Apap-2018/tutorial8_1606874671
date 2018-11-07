package com.apap.tutorial8.controller;

import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.apap.tutorial8.model.UserRoleModel;
import com.apap.tutorial8.service.UserRoleService;

@Controller
@RequestMapping("/user")
public class UserRoleController {
	@Autowired
	private UserRoleService userService;

	public boolean validatePassword (String pass) {
		if(pass.length() >= 8 && Pattern.compile("[a-zA-Z]").matcher(pass).find() && Pattern.compile("[0-9]").matcher(pass).find()) {
			return true;
		}
		return false;
	}
	
	@RequestMapping(value = "/addUser", method = RequestMethod.POST)
	private ModelAndView addUserSubmit(@ModelAttribute UserRoleModel user, RedirectAttributes redirect) {
		String msg="";
		
		if(this.validatePassword(user.getPassword())) {
			userService.addUser(user);
			msg= null;
		}
		else {
			msg="password minimal terdiri dari 8 kata dengan minimal 1 huruf dan angka";
		}
		ModelAndView redirects = new ModelAndView("redirect:/");
		redirect.addFlashAttribute("msg", msg);
		return redirects;
	}

	
	@RequestMapping(value = "/updatePass")
	private String updatePassword() {
		return "update-password";
	}
	
	@RequestMapping(value="/submit", method=RequestMethod.POST)
	private String updatePasswordSubmit(String oldPassword, String newPassword, String confirmPassword, Model model) {
		UserRoleModel user = userService.getUser(SecurityContextHolder.getContext().getAuthentication().getName());
		
		if(newPassword.equals(confirmPassword)) {
			if(userService.isMatch(oldPassword, user.getPassword())) {
				if(this.validatePassword(newPassword)) {
					user.setPassword(newPassword);
					userService.addUser(user);
					model.addAttribute("msg", "Password " + user.getUsername() + " berhasil diubah");

					return "success";
				}
				else {
					model.addAttribute("msg", "password harus terdiri dari 8 karakter dengan minimal 1 angka dan huruf");
				}
				
				return "update-password";
			}
			
			else {
				model.addAttribute("msg", "password lama salah");
				return "update-password";
			}
		}
		else {
			model.addAttribute("msg", "password konfirmasi tidak sesuai");
			return "update-password";
		}
	}
}	
