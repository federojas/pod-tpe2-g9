import ar.edu.itba.pod.models.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SensorFactory {

    public static final String[] days = {
            "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"
    };

    public static final String[] months = {
            "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
    };
    public static final String[] activeSensorNames = {
            "Sensor Villa Crespo",
            "Sensor Villa Luro",
            "Sensor Villa Devoto",
            "Sensor Villa Urquiza",
            "Sensor Villa Pueyrredon",
            "Sensor Villa Santa Rita",
    };
    public static long ACTIVE_SENSOR_STEP = 100L;

    //    In ascending order until length of activeSensorNames
    public static final Long[] activeSensorReadings = {
            ACTIVE_SENSOR_STEP,
            2 * ACTIVE_SENSOR_STEP,
            3 * ACTIVE_SENSOR_STEP,
            4 * ACTIVE_SENSOR_STEP,
            5 * ACTIVE_SENSOR_STEP,
            6 * ACTIVE_SENSOR_STEP,
    };


    public static final String[] inactiveSensorNames = {
            "Sensor Palermo",
            "Sensor Recoleta",
            "Sensor Caballito",
            "Sensor Liniers",
            "Sensor Belgrano",
            "Sensor Nuñez",
    };

    //    This method returns a list of SensorReading active objects
    //    The first one is the one with the lowest readings
    //    The last one is the one with the highest readings
    public static SensorReading[] getActiveSensors() {
        SensorReading[] sensors = new SensorReading[activeSensorNames.length];
        for (int i = 0; i < activeSensorNames.length; i++) {
            sensors[i] = new SensorReading.SensorReadingBuilder(activeSensorReadings[i]).sensorName(activeSensorNames[i]).build();
        }
        return sensors;
    }

    public static long MIN_COUNT = 10000L;
    public static long HIGHEST_COUNT = (long) Math.pow(ACTIVE_SENSOR_STEP, 6);
    public static int Q3_EXPECTED_OUTPUT_LENGTH = 6;
    //    Only the last 3 sensors will satisfy the minimum condition
    public static SensorReading[] getDateTimeReadings() {
        return new SensorReading[]{
                new SensorReading.SensorReadingBuilder((long) Math.pow(ACTIVE_SENSOR_STEP, 0)).year(2018L).month("January").day(1).time(0).sensorName(activeSensorNames[0]).build(),
                new SensorReading.SensorReadingBuilder((long) Math.pow(ACTIVE_SENSOR_STEP, 1)).year(2018L).month("January").day(1).time(0).sensorName(activeSensorNames[1]).build(),
                new SensorReading.SensorReadingBuilder((long) Math.pow(ACTIVE_SENSOR_STEP, 2)).year(2018L).month("January").day(1).time(0).sensorName(activeSensorNames[2]).build(),
                new SensorReading.SensorReadingBuilder((long) Math.pow(ACTIVE_SENSOR_STEP, 3)).year(2018L).month("January").day(1).time(1).sensorName(activeSensorNames[3]).build(),
                new SensorReading.SensorReadingBuilder((long) Math.pow(ACTIVE_SENSOR_STEP, 4)).year(2018L).month("January").day(1).time(2).sensorName(activeSensorNames[4]).build(),
                new SensorReading.SensorReadingBuilder((long) Math.pow(ACTIVE_SENSOR_STEP, 5)).year(2018L).month("January").day(1).time(3).sensorName(activeSensorNames[5]).build(),
                new SensorReading.SensorReadingBuilder(HIGHEST_COUNT).year(2018L).month("January").day(1).time(4).sensorName(activeSensorNames[0]).build(),
                new SensorReading.SensorReadingBuilder(HIGHEST_COUNT).year(2018L).month("January").day(1).time(5).sensorName(activeSensorNames[1]).build(),
                new SensorReading.SensorReadingBuilder(HIGHEST_COUNT).year(2018L).month("January").day(1).time(6).sensorName(activeSensorNames[2]).build(),
        };
    }

    public static List<SensorReading> getSensorMonthReadingsList() {
        List<SensorReading> sensorMonthReadingsList = new ArrayList<>();
        sensorMonthReadingsList.add(new SensorReading.SensorReadingBuilder(100L).month("January").sensorName(activeSensorNames[1]).build());
        sensorMonthReadingsList.add(new SensorReading.SensorReadingBuilder(200L).month("February").sensorName(activeSensorNames[2]).build());
        sensorMonthReadingsList.add(new SensorReading.SensorReadingBuilder(300L).month("March").sensorName(activeSensorNames[1]).build());
        sensorMonthReadingsList.add(new SensorReading.SensorReadingBuilder(400L).month("April").sensorName(activeSensorNames[2]).build());
        sensorMonthReadingsList.add(new SensorReading.SensorReadingBuilder(200L).month("May").sensorName(activeSensorNames[3]).build());
        sensorMonthReadingsList.add(new SensorReading.SensorReadingBuilder(300L).month("March").sensorName(activeSensorNames[4]).build());
        return sensorMonthReadingsList;
    }

    public static List<SensorReading> getDayReadingList () {
        List<SensorReading> toReturn = new ArrayList<>();
        for(int i = 0 ; i < 3 ; i++)
            for(int j = 0 ; j < days.length ; j++)
                toReturn.add(new SensorReading.SensorReadingBuilder( (long) (Math.pow(10, i)*j)).year((long) (2019 + i)).weekDay(days[j]).build());
        return toReturn;
    }





    public static FeasibleMaxMeasure[] getQ3ValidMeasures() {
        return new FeasibleMaxMeasure[] {
                new FeasibleMaxMeasure(HIGHEST_COUNT, 2018L, "January", 1, 4),
                new FeasibleMaxMeasure(HIGHEST_COUNT, 2018L, "January", 1, 5),
                new FeasibleMaxMeasure(HIGHEST_COUNT, 2018L, "January", 1, 6),
                new FeasibleMaxMeasure((long) Math.pow(ACTIVE_SENSOR_STEP, 5), 2018L, "January", 1, 3),
                new FeasibleMaxMeasure((long) Math.pow(ACTIVE_SENSOR_STEP, 4), 2018L, "January", 1, 2),
                new FeasibleMaxMeasure((long) Math.pow(ACTIVE_SENSOR_STEP, 3), 2018L, "January", 1, 1),
        };
    }

//    Already in ascending order by name when there are equals readings
    public static String[] getQ3ValidInOrderSensorNames() {
        return new String[] {
                activeSensorNames[0],
                activeSensorNames[2],
                activeSensorNames[1],
                activeSensorNames[5],
                activeSensorNames[4],
                activeSensorNames[3],
        };
    }

    public static List<SensorReading> getQ5SensorReadingsList() {
        List<SensorReading> sensorReadingsList = new ArrayList<>();

        sensorReadingsList.add(new SensorReading.SensorReadingBuilder(2500000L).sensorName(activeSensorNames[0]).build());
        sensorReadingsList.add(new SensorReading.SensorReadingBuilder(2000000L).sensorName(activeSensorNames[1]).build());
        sensorReadingsList.add(new SensorReading.SensorReadingBuilder(3000000L).sensorName(activeSensorNames[2]).build());
        sensorReadingsList.add(new SensorReading.SensorReadingBuilder(3200000L).sensorName(activeSensorNames[3]).build());
        sensorReadingsList.add(new SensorReading.SensorReadingBuilder(1000000L).sensorName(activeSensorNames[4]).build());
        sensorReadingsList.add(new SensorReading.SensorReadingBuilder(2999999L).sensorName(activeSensorNames[5]).build());
        return sensorReadingsList;
    }
}