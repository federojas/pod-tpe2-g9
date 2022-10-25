import ar.edu.itba.pod.models.DateTimeReading;
import ar.edu.itba.pod.models.DayReading;
import ar.edu.itba.pod.models.FeasibleMaxMeasure;
import ar.edu.itba.pod.models.SensorReading;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SensorFactory {

    public static final String[] days = {
            "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"
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
            sensors[i] = new SensorReading(activeSensorNames[i], activeSensorReadings[i]);
        }
        return sensors;
    }

    public static long MIN_COUNT = 10000L;
    public static long HIGHEST_COUNT = (long) Math.pow(ACTIVE_SENSOR_STEP, 6);
    public static int Q3_EXPECTED_OUTPUT_LENGTH = 6;
    //    Only the last 3 sensors will satisfy the minimum condition
    public static DateTimeReading[] getDateTimeReadings() {
        return new DateTimeReading[]{
                new DateTimeReading((long) Math.pow(ACTIVE_SENSOR_STEP, 0), 2018L, "January", 1, 0, activeSensorNames[0]),
                new DateTimeReading((long) Math.pow(ACTIVE_SENSOR_STEP, 1), 2018L, "January", 1, 0, activeSensorNames[1]),
                new DateTimeReading((long) Math.pow(ACTIVE_SENSOR_STEP, 2), 2018L, "January", 1, 0, activeSensorNames[2]),
                new DateTimeReading((long) Math.pow(ACTIVE_SENSOR_STEP, 3), 2018L, "January", 1, 1, activeSensorNames[3]),
                new DateTimeReading((long) Math.pow(ACTIVE_SENSOR_STEP, 4), 2018L, "January", 1, 2, activeSensorNames[4]),
                new DateTimeReading((long) Math.pow(ACTIVE_SENSOR_STEP, 5), 2018L, "January", 1, 3, activeSensorNames[5]),
                new DateTimeReading(HIGHEST_COUNT, 2018L, "January", 1, 4, activeSensorNames[0]),
                new DateTimeReading(HIGHEST_COUNT, 2018L, "January", 1, 5, activeSensorNames[1]),
                new DateTimeReading(HIGHEST_COUNT, 2018L, "January", 1, 6, activeSensorNames[2]),
        };
    }

    public static List<DayReading> getDayReadingList () {
        List<DayReading> toReturn = new ArrayList<>();
        for(int i = 0 ; i < 3 ; i++)
            for(int j = 0 ; j < days.length ; j++)
                toReturn.add(new DayReading((long) (2019 + i), days[j], (long) (Math.pow(10, i)*j)));
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
}