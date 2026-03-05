package com.taller.backend.repository

import org.springframework.stereotype.Repository
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardOpenOption
import kotlin.io.path.exists
import kotlin.io.path.readText

@Repository
class LocalFileRepository {
    private val storageDir: Path = Paths.get("./storage")
    
    init {
        ensureStorageExists()
    }
    
    fun write(filename: String, content: String) {
        val filePath = storageDir.resolve(filename).normalize()
        Files.writeString(
            filePath,
            content,
            StandardCharsets.UTF_8,
            StandardOpenOption.CREATE,
            StandardOpenOption.TRUNCATE_EXISTING
        )
    }
    
    fun read(filename: String): String {
        val filePath = storageDir.resolve(filename).normalize()
        
        if (!filePath.exists()) {
            throw java.nio.file.NoSuchFileException(filePath.toString())
        }
        
        return filePath.readText(StandardCharsets.UTF_8)
    }
    
    private fun ensureStorageExists() {
        if (!storageDir.exists()) {
            Files.createDirectories(storageDir)
        }
    }
}
