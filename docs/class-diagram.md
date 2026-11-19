
# Class Diagram

แผนภาพแสดงโครงสร้างคลาสและความสัมพันธ์ของระบบที่เพิ่มเข้ามาในการ Refactoring ของ Little Farm

```mermaid
%%{init: {"theme":"base","themeVariables":{"background":"#ffffff","primaryColor":"#e8f1fb","primaryTextColor":"#172b4d","primaryBorderColor":"#315b8a","lineColor":"#263746","secondaryColor":"#e8f5ee","tertiaryColor":"#fff3df","edgeLabelBackground":"#ffffff","fontSize":"14px"}}}%%
classDiagram
    direction TB

    class ProcessingRecipe {
        <<interface>>
        +getProductName() String
        +getCategory() String
        +getRequiredIngredients() Map
        +getRequiredNights() int
        +getSellPrice() int
        +hasEnoughIngredients(Bag) boolean
    }

    class BaseRecipe {
        <<abstract>>
        #productName String
        #category String
        #requiredIngredients Map
        #requiredNights int
        #sellPrice int
        #addRequirement(String, int)
        +hasEnoughIngredients(Bag) boolean
    }

    class CakeRecipe
    class ChipsRecipe

    ProcessingRecipe <|.. BaseRecipe
    BaseRecipe <|-- CakeRecipe
    BaseRecipe <|-- ChipsRecipe

    class ProcessingMachine {
        -state MachineState
        -currentRecipe ProcessingRecipe
        -remainingNights int
        +ProcessingMachine()
        +startProcessing(ProcessingRecipe, Bag)
        +passNights(int)
        +collectProduct(Bag) String
        +getState() MachineState
        +getCurrentRecipe() ProcessingRecipe
        +getRemainingNights() int
        +restoreState(MachineState, ProcessingRecipe, int)
    }

    class MachineState {
        <<enumeration>>
        IDLE
        PROCESSING
        READY
    }

    ProcessingMachine --> ProcessingRecipe : uses
    ProcessingMachine --> MachineState : has state
    ProcessingMachine ..> InsufficientIngredientException : throws
    ProcessingMachine ..> MachineBusyException : throws

    class FarmException {
        <<exception>>
    }

    class InsufficientIngredientException
    class MachineBusyException

    FarmException <|-- InsufficientIngredientException
    FarmException <|-- MachineBusyException

    class WeatherStrategy {
        <<interface>>
        +forecast(Random) Weather
    }

    class NormalWeatherStrategy
    class RainyWeatherStrategy

    class WeatherStrategyFactory {
        +create(String)$ WeatherStrategy
    }

    class Weather {
        <<enumeration>>
        SUNNY
        RAINY
    }

    WeatherStrategy <|.. NormalWeatherStrategy
    WeatherStrategy <|.. RainyWeatherStrategy
    WeatherStrategyFactory ..> WeatherStrategy : creates
    NormalWeatherStrategy ..> Weather : returns
    RainyWeatherStrategy ..> Weather : returns

    class Main
    class GamePanel
    class Screen {
        <<interface>>
        +keyPressed(int)
        +update()
        +draw(Graphics2D)
    }
    class FarmScreen
    class GameClock {
        -day int
        -minutes int
        -weather Weather
        -weatherStrategy WeatherStrategy
        +GameClock()
        +GameClock(WeatherStrategy)
        +tick()
        +nextDay(Random)
        +getDay() int
        +getMinutes() int
        +getWeather() Weather
        +setWeather(Weather)
        +darkness() int
        +timeText() String
    }

    Main ..> WeatherStrategyFactory : creates strategy
    Main ..> GamePanel : creates
    GamePanel --> WeatherStrategy : stores
    GamePanel ..> Screen : manages screens
    Screen <|.. FarmScreen
    FarmScreen --> GamePanel : uses
    FarmScreen --> GameClock : creates
    GameClock --> WeatherStrategy : uses
    GameClock --> Weather : stores forecast
```

## Explanation

### 1. Recipe System
- `ProcessingRecipe` เป็น Interface กำหนดเมธอดที่สูตรแปรรูปต้องมี
- `BaseRecipe` เป็น Abstract Class ที่ implements `ProcessingRecipe`
- `CakeRecipe` และ `ChipsRecipe` สืบทอดจาก `BaseRecipe`

### 2. Processing Machine System
- `ProcessingMachine` จัดการการเริ่มผลิต การนับคืน และการรับสินค้า
- `MachineState` กำหนดสถานะ IDLE, PROCESSING และ READY
- เครื่องแปรรูปใช้ `ProcessingRecipe` เพื่ออ่านข้อมูลสูตร
- `FarmException` เป็นคลาส Exception หลักของระบบ
- `InsufficientIngredientException` และ `MachineBusyException` เป็น Exception ที่สืบทอดจาก `FarmException`

### 3. Weather Strategy System
- `WeatherStrategy` เป็น Interface สำหรับกำหนดรูปแบบการสุ่มสภาพอากาศ
- `NormalWeatherStrategy` และ `RainyWeatherStrategy` เป็น Strategy ที่มีเงื่อนไขการสุ่มต่างกัน
- `WeatherStrategyFactory` สร้าง Strategy ตามโหมดที่เลือก
- `Main` ส่ง Strategy ไปยัง `GamePanel`
- `FarmScreen` สร้าง `GameClock` โดยส่ง Strategy จาก `GamePanel`
- `GameClock` ใช้ Strategy เพื่อสุ่มสภาพอากาศของวันถัดไป

### 4. Screen System
- `Screen` เป็น Interface กำหนดโครงสร้างพื้นฐานของหน้าจอเกม
- `FarmScreen` implements `Screen`
- `GamePanel` จัดการหน้าจอเกมและเปลี่ยนหน้าจอตามสถานะของเกม

## Existing Classes Modified During Refactoring

คลาสเดิมที่มีการแก้ไขเพื่อรองรับโครงสร้างใหม่ของระบบ มีทั้งหมด 4 คลาส

| Class | Modification |
|---|---|
| `FarmScreen` | ปรับการสร้าง GameClock ให้รับ WeatherStrategy จาก GamePanel |
| `GameClock` | เพิ่ม Constructor ที่รับ WeatherStrategy และใช้ Strategy ในการสุ่มอากาศของวันถัดไป |
| `GamePanel` | เพิ่มการจัดเก็บ WeatherStrategy และ Getter สำหรับส่ง Strategy ให้ส่วนอื่นใช้งาน |
| `Main` | เพิ่มการเลือก WeatherStrategy ผ่าน WeatherStrategyFactory ก่อนสร้าง GamePanel |
