package me.viharev.courseworkpartthree;

import me.viharev.courseworkpartthree.models.*;
import me.viharev.courseworkpartthree.repository.*;
import me.viharev.courseworkpartthree.services.SocksService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class SocksServiceTest {

    private final SocksService socksService;
    private final StorageUnitRepository storageUnitRepository = Mockito.mock(StorageUnitRepository.class);
    private final TransactionRepository transactionRepository = Mockito.mock(TransactionRepository.class);

    SocksColor socksColor = SocksColor.BLACK;
    SocksSize socksSize = SocksSize.S;
    int cottonPart = 55;
    int cottonMin = 40;
    int cottonMax = 60;
    int expectedValueSocks = 10;
    int quantity = 100;


    public SocksServiceTest() {
        socksService = new SocksService(transactionRepository, storageUnitRepository);
    }

    @Test
    @DisplayName("Проверка корректной работы метода по поиску носков с содержанием хлопка МЕНЬШЕ заданной величины")
    void correctFindByCottonPartLessThan() {
        when (storageUnitRepository.findByCottonPartLessThan(socksColor, socksSize, cottonMin)).thenReturn(expectedValueSocks);
        int socksValue = socksService.findByCottonPartLessThan(socksColor, socksSize, cottonMin);
        assertEquals(expectedValueSocks, socksValue);
    }

    @Test
    @DisplayName("Проверка корректной работы метода по поиску носков с содержанием хлопка БОЛЬШЕ заданной величины")
    void correctFindByCottonPartMoreThanTesting() {
        when (storageUnitRepository.findByCottonPartMoreThan(socksColor, socksSize, cottonMax)).thenReturn(expectedValueSocks);
        int socksValue = socksService.findByCottonPartMoreThan(socksColor, socksSize, cottonMax);
        assertEquals(expectedValueSocks, socksValue);
    }

    @Test
    @DisplayName("Проверка метода добавления носков в список (хранилище) и добавления транзакции (поступившие носки)")
    void testingAddSocksToStorage() {
        when(storageUnitRepository.addInStorageUnitRepository(cottonPart,socksColor,socksSize,quantity)).thenReturn(true);
        when(transactionRepository.addTransaction(cottonPart, socksColor, socksSize, quantity, TransactionsType.INCOMING)).thenReturn(true);
        boolean correctAddInStorage = socksService.addToStorage(socksColor,socksSize, cottonPart,quantity);
        assertTrue(correctAddInStorage);
    }

    @Test
    @DisplayName("Проверка метода удаления(списания) носков из списка (хранилища) и добавления транзакции(списанные носки)")
    void correctDeleteFromStorage() {
        when(storageUnitRepository.delete(cottonPart,socksColor,socksSize,quantity)).thenReturn(true);
        when(transactionRepository.addTransaction(cottonPart, socksColor, socksSize, quantity, TransactionsType.CANCELLATION)).thenReturn(true);
        boolean correctDeleteFromStorage = socksService.delete(socksColor,socksSize, cottonPart,quantity);
        assertTrue(correctDeleteFromStorage);
    }

    @Test
    @DisplayName("Проверка метода отпуска партии носков со склада и добавления транзакции (отправленные со склада носки)")
    void correctReleaseFromStorage() {
        when(storageUnitRepository.outFromStorage(cottonPart,socksColor,socksSize,quantity)).thenReturn(true);
        when(transactionRepository.addTransaction(cottonPart, socksColor, socksSize, quantity, TransactionsType.OUTGOING)).thenReturn(true);
        boolean correctReleaseFromStorage = socksService.releaseFromStorage(socksColor,socksSize,cottonPart,quantity);
        assertTrue(correctReleaseFromStorage);
    }
}