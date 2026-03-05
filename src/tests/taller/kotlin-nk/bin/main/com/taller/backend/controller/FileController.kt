package com.taller.backend.controller

import com.taller.backend.dto.UploadRequest
import com.taller.backend.service.FileService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/files")
class FileController(private val fileService: FileService) {
    
    @PostMapping
    fun uploadFile(@RequestBody request: UploadRequest): ResponseEntity<Unit> {
        fileService.saveFile(request.filename, request.content)
        return ResponseEntity.ok().build()
    }
    
    @GetMapping("/{filename}")
    fun downloadFile(@PathVariable filename: String): ResponseEntity<String> {
        val content = fileService.readFile(filename)
        return ResponseEntity.ok(content)
    }
}
