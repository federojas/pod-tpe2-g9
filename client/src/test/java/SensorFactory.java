import ar.edu.itba.pod.models.SensorReading;

public class SensorFactory {
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
}
