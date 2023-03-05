package bg.fmi.services;

import bg.fmi.payload.response.ConfigResponse;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.*;

@Service
public class FileService {

    public byte[] exportCSV(List<ConfigResponse> configResponses) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
        CSVWriter csvWriter = new CSVWriter(outputStreamWriter);

        String[] headerRow = {"Name", "Description", "Usernames", "Systemnames", "Configurations"};
        csvWriter.writeNext(headerRow);

        for (ConfigResponse configResponse : configResponses) {
            String name = configResponse.getName();
            String description = configResponse.getDescription();
            String username = String.join(",", configResponse.getUsernames());
            String systemname = String.join(",", configResponse.getSystemnames());
            String configurations = configResponse.getConfigurations().entrySet().stream()
                    .map(entry -> entry.getKey() + ":" + entry.getValue())
                    .reduce("", (acc, entry) -> acc + ";" + entry);

            String[] dataRow = {name, description, username, systemname, configurations};
            csvWriter.writeNext(dataRow);
        }

        csvWriter.flush();
        csvWriter.close();

        byte[] bytes = outputStream.toByteArray();

        return bytes;
    }

    public List<ConfigResponse> importCSV(MultipartFile file) throws IOException, CsvException {
        try (Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            CSVReader csvReader = new CSVReader(reader);
            List<String[]> rows = csvReader.readAll();
            List<ConfigResponse> configResponses = new ArrayList<>();
            for (int i = 1; i < rows.size(); i++) {
                String[] row = rows.get(i);
                ConfigResponse configResponse = ConfigResponse.builder()
                        .name(row[0])
                        .description(row[1])
                        .usernames(Arrays.asList(row[2].split(",")))
                        .systemnames(Arrays.asList(row[3].split(",")))
                        .configurations(parseConfigurations(row[4]))
                        .build();
                configResponses.add(configResponse);
            }
            return configResponses;
        }
    }

    private Map<String, String> parseConfigurations(String configString) {
        Map<String, String> configurations = new HashMap<>();
        String[] configPairs = configString.split(";");
        for (String pair : configPairs) {
            String[] parts = pair.split(":");
            if (parts.length == 2) {
                configurations.put(parts[0], parts[1]);
            }
        }
        return configurations;
    }
}
