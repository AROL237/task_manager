export default interface ApiResponse{
	code: Number,
	message: String,
	success: Boolean,
	timestamp: Date,
	data: any
}