@Composable
private fun StatItem(emoji: String, value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(emoji, fontSize = 18.sp)
        Text(value, color = Gold, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        Text(label, color = TextSecondaryDark, fontSize = 10.sp)
    }
}