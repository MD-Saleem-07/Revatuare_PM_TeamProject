package com.revature.pm.service;

import java.util.List;

import com.revature.pm.dto.DashboardStatsDTO;
import com.revature.pm.dto.PasswordEntryDTO;
import com.revature.pm.dto.ViewPasswordDTO;

public interface PasswordEntryService {

    void addPasswordByUsername(String username, PasswordEntryDTO dto);

    List<PasswordEntryDTO> getAllPasswordsByUsername(String username);

    void updatePasswordByUsername(String username, Long entryId, PasswordEntryDTO dto);

    void deletePasswordByUsername(String username, Long entryId);

    void toggleFavoriteByUsername(String username, Long entryId);

    List<PasswordEntryDTO> getFavoritePasswordsByUsername(String username);

    String viewPasswordByUsername(String username, Long entryId, ViewPasswordDTO dto);

    List<PasswordEntryDTO> searchPasswordsByUsername(String username, String keyword);

    List<PasswordEntryDTO> filterByCategoryByUsername(String username, String category);

    List<PasswordEntryDTO> sortPasswordsByUsername(String username, String sortBy);

    DashboardStatsDTO getDashboardStatsByUsername(String username);
}