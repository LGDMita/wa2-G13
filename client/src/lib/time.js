import dayjs from 'dayjs';
import isSameOrBefore from 'dayjs/plugin/isSameOrBefore';
dayjs.extend(isSameOrBefore);

const format=datetime=>dayjs(datetime).format("DD/MM/YYYY HH:mm");

const isStillValid=datetime=>dayjs().isSameOrBefore(datetime);

export default{
    format,
    isStillValid,
}