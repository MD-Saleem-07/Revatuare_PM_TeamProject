package com.revature.pm.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "security_questions")
public class SecurityQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String question;

    @Column(nullable = false)
    private String encryptedAnswer;   // Will store encrypted or hashed answer

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public SecurityQuestion() {
    }

    public SecurityQuestion(Long id, String question, String encryptedAnswer, User user) {
        this.id = id;
        this.question = question;
        this.encryptedAnswer = encryptedAnswer;
        this.user = user;
    }
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getEncryptedAnswer() {
        return encryptedAnswer;
    }

    public void setEncryptedAnswer(String encryptedAnswer) {
        this.encryptedAnswer = encryptedAnswer;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}