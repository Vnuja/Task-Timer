import android.os.Parcel
import android.os.Parcelable

data class Task(
    val name: String,
    val description: String,
    val priority: String,
    val completionTime: String,
    val category: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(description)
        parcel.writeString(priority)
        parcel.writeString(completionTime)
        parcel.writeString(category)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<Task> {
        override fun createFromParcel(parcel: Parcel): Task = Task(parcel)

        override fun newArray(size: Int): Array<Task?> = arrayOfNulls(size)
    }
}
