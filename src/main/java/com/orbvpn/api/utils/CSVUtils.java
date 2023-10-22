package com.orbvpn.api.utils;

import com.orbvpn.api.domain.dto.BulkSubscription;
import com.orbvpn.api.domain.dto.BulkUserCreate;
import com.orbvpn.api.domain.entity.User;
import com.orbvpn.api.domain.entity.UserProfile;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CSVUtils {

    public static BulkUserCreate csvToUsers(InputStream is) {
        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
             CSVParser csvParser = new CSVParser(fileReader,
                     CSVFormat.newFormat(';').withHeader())) {

            List<User> users = new ArrayList<>();
            List<UserProfile> profiles = new ArrayList<>();
            List<BulkSubscription> subscriptions = new ArrayList<>();

            Iterable<CSVRecord> csvRecords = csvParser.getRecords();

            for (CSVRecord csvRecord : csvRecords) {
                User user = new User();
                UserProfile profile = new UserProfile();
                BulkSubscription subscription = new BulkSubscription();

                user.setEmail(csvRecord.get("Email"));
                user.setPassword(csvRecord.get("Password"));
                user.setUsername(csvRecord.get("Username"));

                profile.setFirstName(csvRecord.get("First Name"));
                profile.setLastName(csvRecord.get("Last Name"));
                profile.setAddress(csvRecord.get("Address"));
                profile.setCity(csvRecord.get("City"));
                profile.setCountry(csvRecord.get("Country"));
                profile.setPostalCode(csvRecord.get("Zip Code"));
                profile.setPhone(csvRecord.get("Phone"));
                profile.setBirthDate(LocalDate.parse(csvRecord.get("Birth Date")));

                subscription.setDuration(Integer.parseInt(csvRecord.get("Duration")));
                subscription.setMultiLoginCount(Integer.parseInt(csvRecord.get("Devices")));
                subscription.setGroupId(Integer.parseInt(csvRecord.get("Group")));

                subscriptions.add(subscription);
                profiles.add(profile);
                users.add(user);
            }

        return new BulkUserCreate(users, profiles, subscriptions);
        } catch (Exception e) {
            throw new RuntimeException("Could not parse the file. " + e.getMessage());
        }
    }
}
