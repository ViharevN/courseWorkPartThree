package me.viharev.courseworkpartthree;

import me.viharev.courseworkpartthree.services.TransactionsFileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.api.DisplayName;
import org.springframework.test.util.ReflectionTestUtils;
import static org.junit.jupiter.api.Assertions.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

class TransactionsFileServiceTest {

    @TempDir
    File tempDirForTesting;
    private String nameJsonFileForTesting;
    private String pathJsonFileForTesting;

    private String nameTXTFileForTesting;
    private String pathTXTFileForTesting;

    private final TransactionsFileService transactionsFileService;

    TransactionsFileServiceTest() {
        transactionsFileService = new TransactionsFileService();
    }

    @BeforeEach
    void setUp(){
        //Устанавливаем путь и название файла для Json файла
        ReflectionTestUtils.setField(transactionsFileService, "transactionsListFilePath", tempDirForTesting.getPath());
        ReflectionTestUtils.setField(transactionsFileService, "transactionsListFileName", "transactions.json");
        //Устанавливаем путь и название файла для TXT файла
        ReflectionTestUtils.setField(transactionsFileService, "transactionsTxtFilePath", tempDirForTesting.getPath());
        ReflectionTestUtils.setField(transactionsFileService, "transactionsTxtFileName", "transactions.txt");
        //Присвоим переменной для Json файла значения
        nameJsonFileForTesting = transactionsFileService.getTransactionsListFileName();
        pathJsonFileForTesting = transactionsFileService.getTransactionsListFilePath();
        //Присвоим переменной для TXT файла значения
        nameTXTFileForTesting = transactionsFileService.getTransactionsTxtFileName();
        pathTXTFileForTesting = transactionsFileService.getTransactionsTxtFilePath();
    }

    @Test
    @DisplayName("Проверка работы метода по очистке Json файла со списком транзакций")
    void cleanTransactionsListJson() throws IOException {
        //Проверим работу тестируемого метода
        assertTrue(transactionsFileService.cleanTransactionsListJson());
        //Должен появиться файл, проверим это
        assertTrue(Files.exists(Path.of(pathJsonFileForTesting, nameJsonFileForTesting)));
        //Удалим файл
        assertTrue(Files.deleteIfExists(Path.of(pathJsonFileForTesting, nameJsonFileForTesting)));
        //Проверим удаление файла
        assertFalse(Files.exists(Path.of(pathJsonFileForTesting, nameJsonFileForTesting)));
    }

    @Test
    @DisplayName("Проверка работы метода по очистке Txt файла со списком транзакций")
    void cleanTransactionsListTxt() throws IOException {
        //Проверим работу тестируемого метода
        assertTrue(transactionsFileService.cleanTransactionsTxt());
        //Должен появиться файл, проверим это
        assertTrue(Files.exists(Path.of(pathTXTFileForTesting, nameTXTFileForTesting)));
        //Удалим файл
        assertTrue(Files.deleteIfExists(Path.of(pathTXTFileForTesting, nameTXTFileForTesting)));
        //Проверим удаление файла
        assertFalse(Files.exists(Path.of(pathTXTFileForTesting, nameTXTFileForTesting)));
    }

    @Test
    @DisplayName("Проверка работы метода по получению Txt файла со списком транзакций")
    void getTxtFile() {
        //Создадим файл, который будем считывать и проверять
        File testFile = new File(pathTXTFileForTesting + "/" + nameTXTFileForTesting);
        assertEquals(testFile, transactionsFileService.getTxtFile());
    }

    @Test
    @DisplayName("Проверка работы метода по получению Json файла со списком транзакций")
    void getTransactionsListJson() {
        //Создадим файл, который будем считывать и проверять
        File testFile = new File(pathJsonFileForTesting + "/" + nameJsonFileForTesting);
        assertEquals(testFile, transactionsFileService.getTransactionsListJson());
    }

    @Test
    @DisplayName("Проверка работы метода по сохранению (записи) Json файла со списком транзакций")
    void saveTransactionsListToJsonFile() {
        String stringForSave = "string for save";
        assertTrue(transactionsFileService.saveTransactionsListToJsonFile(stringForSave));
    }

    @Test
    @DisplayName("Проверка работы метода по сохранению (записи) Txt файла со списком транзакций")
    void saveTransactionsToTxtFile() {
        String stringForSave = "string for save";
        assertTrue(transactionsFileService.saveTransactionsToTxtFile(stringForSave));
    }

    @Test
    @DisplayName("Проверка работы метода по чтению сохраненного Json файла со списком транзакций")
    void readTransactionsListFromJsonFile() throws IOException {
        String stringForSave = "string for save";
        //Для проверки надо что-нибудь записать в файл, чтобы потом считать
        transactionsFileService.saveTransactionsListToJsonFile(stringForSave);
        String savedString = Files.readString(Path.of(pathJsonFileForTesting, nameJsonFileForTesting));
        assertEquals(stringForSave, savedString);
    }
}