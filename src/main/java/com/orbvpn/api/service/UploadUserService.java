package com.orbvpn.api.service;

import com.orbvpn.api.domain.entity.Group;
import com.orbvpn.api.domain.entity.Reseller;
import com.orbvpn.api.domain.entity.Role;
import com.orbvpn.api.domain.entity.User;
import com.orbvpn.api.domain.entity.UserProfile;
import com.orbvpn.api.domain.entity.UserSubscription;
import com.orbvpn.api.domain.enums.PaymentStatus;
import com.orbvpn.api.domain.enums.PaymentType;
import com.orbvpn.api.domain.enums.RoleName;
import com.orbvpn.api.reposiitory.GroupRepository;
import com.orbvpn.api.reposiitory.ResellerRepository;
import com.orbvpn.api.reposiitory.UserRepository;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Row.MissingCellPolicy;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.NumberToTextConverter;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.hibernate.validator.internal.constraintvalidators.bv.EmailValidator;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UploadUserService {

  private static final Map<String, Integer> resellerMap = Map.of("OrbVPN", 1,
    "Hosseing Aghanassir", 2, "Ali Sadeghi", 3);

  private final UserRepository userRepository;
  private final ResellerRepository resellerRepository;
  private final GroupRepository groupRepository;
  private final PasswordEncoder passwordEncoder;
  private final UserSubscriptionService userSubscriptionService;
  private final RoleService roleService;
  private final PasswordService passwordService;

  public boolean uploadUsers(InputStream inputStream) {

    try {
      Workbook workbook = new XSSFWorkbook(inputStream);
      Sheet datatypeSheet = workbook.getSheetAt(0);
      Iterator<Row> iterator = datatypeSheet.iterator();

      log.info("Number of rows: {}", datatypeSheet.getPhysicalNumberOfRows());

      // Ignore column names
      iterator.next();
      while (iterator.hasNext()) {

        Row currentRow = iterator.next();

        Cell usernameCell = currentRow.getCell(1, MissingCellPolicy.CREATE_NULL_AS_BLANK);
        String username = usernameCell.getStringCellValue();

        if (StringUtils.isBlank(username)) {
          continue;
        }

        Cell emailCell = currentRow.getCell(14, MissingCellPolicy.CREATE_NULL_AS_BLANK);
        String email = emailCell.getStringCellValue();
        if (!isValidEmail(email)) {
          email = "invalid@mail.com";
        }

        Cell passwordCell = currentRow.getCell(2, MissingCellPolicy.CREATE_NULL_AS_BLANK);
        String password;
        if (passwordCell.getCellType() == CellType.NUMERIC) {
          password = NumberToTextConverter.toText(passwordCell.getNumericCellValue());
        } else {
          password = passwordCell.getStringCellValue();
        }

        if (StringUtils.isBlank(password)) {
          password = "123456";
        }

        Cell resellerCell = currentRow.getCell(4, MissingCellPolicy.CREATE_NULL_AS_BLANK);
        String reseller = resellerCell.getStringCellValue();

        Cell servicePackageCell = currentRow.getCell(5, MissingCellPolicy.CREATE_NULL_AS_BLANK);
        String servicePackage = servicePackageCell.getStringCellValue();

        Cell groupNameCell = currentRow.getCell(3, MissingCellPolicy.CREATE_NULL_AS_BLANK);
        String groupName = groupNameCell.getStringCellValue();

        Cell multiLoginCountCell = currentRow.getCell(11, MissingCellPolicy.CREATE_NULL_AS_BLANK);
        double multiLoginCount = multiLoginCountCell.getNumericCellValue();
        if (multiLoginCount == 0) {
          multiLoginCount = 2.0;
        }

        Cell creationDateCell = currentRow.getCell(10, MissingCellPolicy.CREATE_NULL_AS_BLANK);
        Date creationDate = new Date();
        try {
          creationDate = creationDateCell.getDateCellValue();
        } catch (Exception ex) {
          log.info("Exception:{}", ex.getMessage());
        }
        LocalDateTime creationDateTime = getCreationDate(creationDate);

        Cell expirationDateCell = currentRow.getCell(7, MissingCellPolicy.CREATE_NULL_AS_BLANK);
        Date expirationDate = expirationDateCell.getDateCellValue();
        LocalDateTime expirationDateTime = getCreationDate(expirationDate);

        Cell fullNameCell = currentRow.getCell(12, MissingCellPolicy.CREATE_NULL_AS_BLANK);
        String fullName = fullNameCell.getStringCellValue();
        String[] names = fullName.split(" ");

        Cell cellPhoneCell = currentRow.getCell(13, MissingCellPolicy.CREATE_NULL_AS_BLANK);
        String cellPhone;
        if (cellPhoneCell.getCellType() == CellType.NUMERIC) {
          cellPhone = NumberToTextConverter.toText(cellPhoneCell.getNumericCellValue());
        } else {
          cellPhone = cellPhoneCell.getStringCellValue();
        }

        Cell postalCodeCell = currentRow.getCell(15, MissingCellPolicy.CREATE_NULL_AS_BLANK);
        String postalCode;
        if (postalCodeCell.getCellType() == CellType.NUMERIC) {
          postalCode = NumberToTextConverter.toText(postalCodeCell.getNumericCellValue());
        } else {
          postalCode = postalCodeCell.getStringCellValue();
        }

        Cell cityCell = currentRow.getCell(16, MissingCellPolicy.CREATE_NULL_AS_BLANK);
        String city = cityCell.getStringCellValue();

        Cell countryCell = currentRow.getCell(17, MissingCellPolicy.CREATE_NULL_AS_BLANK);
        String country = countryCell.getStringCellValue();

        User user = new User();

        user.setUsername(username);
        user.setEmail(email);
        passwordService.setPassword(user, password);
        Role role = roleService.getByName(RoleName.USER);
        user.setRole(role);

        UserProfile userProfile = new UserProfile();
        if(names.length> 0) {
          userProfile.setFirstName(names[0]);
        }
        if(names.length > 1) {
          userProfile.setLastName(names[1]);
        }
        userProfile.setPhone(cellPhone);
        userProfile.setPostalCode(postalCode);
        userProfile.setCity(city);
        userProfile.setCountry(country);
        userProfile.setUser(user);
        user.setProfile(userProfile);

        // Set resellers
        Integer resellerId = resellerMap.getOrDefault(reseller, 1);
        Reseller resellerEnt = resellerRepository.getOne(resellerId);
        user.setReseller(resellerEnt);

        userRepository.save(user);
        user.setCreatedAt(creationDateTime);
        userRepository.save(user);
        // Create subscription
        Integer groupId = getGroupId(groupName, servicePackage);
        Group group = groupRepository.getOne(groupId);
        String paymentId = UUID.randomUUID().toString();
//        UserSubscription userSubscription = userSubscriptionService
//          .createUserSubscription(user, group, PaymentType.RESELLER_CREDIT, PaymentStatus.PENDING,
//            paymentId);
//
//        userSubscription.setMultiLoginCount((int) multiLoginCount);
//        userSubscription.setExpiresAt(expirationDateTime);
//        userSubscriptionService.fullFillSubscription(userSubscription);
      }
    } catch (IOException ioException) {
      throw new RuntimeException("Can not upload users");
    }

    return true;
  }

  private static final Pattern EMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$");

  private boolean isValidEmail(String email) {
    Matcher m = EMAIL_PATTERN.matcher(email);
    return m.matches();
  }

  private static final Map<String, Integer> agentServiceMap = Map
    .of("3 Months", 3, "6 Months", 4 ,"1 Year", 5,
      "2 Years", 6, "3 Years", 7);
  private static final Map<String, Integer> iranServiceMap = Map
    .of("3 Months", 15, "6 Months", 16 ,"1 Year", 17,
      "2 Years", 18, "3 Years", 19);



  private int getGroupId(String groupName, String servicePackage) {
    if("Agent Service".equals(servicePackage)) {
      return agentServiceMap.getOrDefault(groupName, 1);
    }

    if("Iran Service".equals(servicePackage)) {
      return iranServiceMap.getOrDefault(groupName,2);
    }

    return 1;
  }

  private LocalDateTime getCreationDate(Date date) {
    if(date == null) {
      return LocalDateTime.now();
    }

    return new java.sql.Timestamp(
      date.getTime()).toLocalDateTime();
  }

}
