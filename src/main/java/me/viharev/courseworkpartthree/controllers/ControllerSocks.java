package me.viharev.courseworkpartthree.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import me.viharev.courseworkpartthree.services.SocksService;
import me.viharev.courseworkpartthree.models.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController()
@RequestMapping("socks")
@Tag(name = "Наименование товара - 'Носки'", description = "CRUD-операции для работы с товаром 'Носки'")
public class ControllerSocks {

    private final SocksService socksService;

    public ControllerSocks(SocksService socksService) {
        this.socksService = socksService;
    }

    @PostMapping("/incoming/{socksColor}/{socksSize}/{cottonPart}/{quantity}")
    @Operation(summary = "Добавление носков на склад")
    public ResponseEntity<String> incoming(@PathVariable("socksColor") SocksColor socksColor,
                                          @PathVariable("socksSize") SocksSize socksSize,
                                          @PathVariable("cottonPart") int cottonPart,
                                          @PathVariable("quantity") int quantity) {
        {
            if (socksService.addToStorage(socksColor, socksSize, cottonPart, quantity)) {
                return ResponseEntity.status(HttpStatus.OK).body("Партия носков добавлена на склад");
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Партия носков НЕ добавлена. Ошибка 400 - Bad Request");
        }
    }

    @GetMapping("viewQuantityCottonMin/{socksColor}/{socksSize}/{cottonMin}")
    @Operation(summary = "Получение сведений о наличии товара на складе по содержанию хлопка меньше указанного")
    public ResponseEntity<String> findCottonPartMin(@PathVariable("socksColor") SocksColor socksColor,
                                                    @PathVariable("socksSize") SocksSize socksSize,
                                                    @PathVariable("cottonMin") int cottonMin) {
        int quantity = socksService.findByCottonPartLessThan(socksColor, socksSize, cottonMin);
        if (quantity > 0) {
            return ResponseEntity.ok().body("По запросу найдено " + quantity + " шт. товара");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("По запросу ничего не найдено");
        }
    }

    @GetMapping("viewQuantityCottonMax/{socksColor}/{socksSize}/{cottonMax}")
    @Operation(summary = "Получение сведений о наличии товара на складе по содержанию хлопка больше указанного")
    public ResponseEntity<String> findCottonPartMax(@PathVariable("socksColor") SocksColor socksColor,
                                                    @PathVariable("socksSize") SocksSize socksSize,
                                                    @PathVariable("cottonMax") int cottonMax) {
        int quantity = socksService.findByCottonPartMoreThan(socksColor, socksSize, cottonMax);
        if (quantity > 0) {
            return ResponseEntity.ok().body("По запросу найдено " + quantity + " шт. товара");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("По запросу ничего не найдено");
        }
    }

    @PutMapping("outgoing/{socksColor}/{socksSize}/{cottonPart}/{quantity}")
    @Operation(summary = "Отпуск со склада партии товара (носков)")
    public ResponseEntity<String> outgoing(@PathVariable("socksColor") SocksColor socksColor,
                                          @PathVariable("socksSize") SocksSize socksSize,
                                          @PathVariable("cottonPart") int cottonPart,
                                          @PathVariable("quantity") int quantity) {
        if (socksService.releaseFromStorage(socksColor, socksSize, cottonPart, quantity)) {
            return ResponseEntity.ok().body("Товар со склада отпущен");
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Товар со склада НЕ отпущен. Возможно, его слишком мало или нет. Ошибка 400 - Bad Request");
    }

    @DeleteMapping("cancellation/{socksColor}/{socksSize}/{cottonPart}/{quantity}")
    @Operation(summary = "Удаление бракованной партии товара (носков)")
    public ResponseEntity<String> cancellation(@PathVariable("socksColor") SocksColor socksColor,
                                         @PathVariable("socksSize") SocksSize socksSize,
                                         @PathVariable("cottonPart") int cottonPart,
                                         @PathVariable("quantity") int quantity) {
        if (socksService.delete(socksColor, socksSize, cottonPart, quantity)) {
            return ResponseEntity.ok().body("Брак списан со склада.");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Указанная партия носков не найдена. Ошибка 404 - Not Found");
    }

}
