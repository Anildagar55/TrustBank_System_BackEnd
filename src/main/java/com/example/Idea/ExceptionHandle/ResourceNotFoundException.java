package com.example.Idea.ExceptionHandle;

public class ResourceNotFoundException extends RuntimeException{
    public ResourceNotFoundException(String mess){
        super(mess);
    }
}
