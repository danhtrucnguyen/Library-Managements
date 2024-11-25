package com.library.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.library.model.Publisher;
import com.library.service.PublisherService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin")
public class PublisherController {
	
	@Autowired
	private PublisherService publisherService;
	
	@GetMapping("/publisher")
	public String category(Model m) {
		m.addAttribute("publishers", publisherService.getAllPublisher());
		return "admin/publisher";
	}

	@PostMapping("/savePublisher")
	public String savePublisher(@ModelAttribute Publisher publisher, @RequestParam("file") MultipartFile file,
			HttpSession session) throws IOException {

		String imageName = file != null ? file.getOriginalFilename() : "default.jpg";
		publisher.setImageName(imageName);

		Boolean existPublisher = publisherService.existPublisher(publisher.getImageName());

		if (existPublisher) {
			session.setAttribute("errorMsg", "Tên Nhà Xuất Bản đã tồn tại");
		} else {

			Publisher savePublisher = publisherService.savePublisher(publisher);

			if (ObjectUtils.isEmpty(savePublisher)) {
				session.setAttribute("errorMsg", "Lưu không thành ông ! lỗi máy chủ");
			} else {

				File saveFile = new ClassPathResource("static/images").getFile();

				Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "publisher_img" + File.separator
						+ file.getOriginalFilename());

				// System.out.println(path);
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

				session.setAttribute("succMsg", "Lưu thành công");
			}
		}

		return "redirect:/admin/publisher";
	}

	@GetMapping("/deletePublisher/{id}")
	public String deletePublisher(@PathVariable int id, HttpSession session) {
		Boolean deletePublisher = publisherService.deletePublisher(id);

		if (deletePublisher) {
			session.setAttribute("succMsg", "NXB đã được xóa thành công");
		} else {
			session.setAttribute("errorMsg", "Có lỗi xảy ra");
		}

		return "redirect:/admin/publisher";
	}

	@GetMapping("/loadEditPublisher/{id}")
	public String loadEditPublisher(@PathVariable int id, Model m) {
		m.addAttribute("publisher", publisherService.getPublisherById(id));
		return "admin/edit_publisher";
	}

	@PostMapping("/updatePublisher")
	public String updatePublisher(@ModelAttribute Publisher publisher, @RequestParam("file") MultipartFile file,
			HttpSession session) throws IOException {

		Publisher oldPublisher = publisherService.getPublisherById(publisher.getId());
		String imageName = file.isEmpty() ? oldPublisher.getImageName() : file.getOriginalFilename();

		if (!ObjectUtils.isEmpty(publisher)) {

			oldPublisher.setName(publisher.getName());
			oldPublisher.setIsActive(publisher.getIsActive());
			oldPublisher.setImageName(imageName);
		}

		Publisher updatePublisher = publisherService.savePublisher(oldPublisher);

		if (!ObjectUtils.isEmpty(updatePublisher)) {

			if (!file.isEmpty()) {
				File saveFile = new ClassPathResource("static/images").getFile();

				Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "publisher_img" + File.separator
						+ file.getOriginalFilename());

				// System.out.println(path);
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
			}

			session.setAttribute("succMsg", "Danh mục sách cập nhật thành công");
		} else {
			session.setAttribute("errorMsg", "Danh mục sách cập nhật không thành công");
		}

		return "redirect:/admin/loadEditPublisher/" + publisher.getId();
	}

}
