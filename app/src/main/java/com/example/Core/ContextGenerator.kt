package com.example.Core

enum class Category {
    GENERAL_KNOWLEDGE,
    EDUCATION_AND_LEARNING,
    TRAVEL_AND_ADVENTURE,
    COMEDY,
    SCIENCE_AND_TECHNOLOGY,
    FOOD_AND_COOKING,
    HEALTH_AND_WELLNESS,
    BOOKS
}

class CategoryInfo(val name: String, val description: String) {
    override fun toString(): String {
        return "Category: $name\nDescription: $description"
    }
}

fun getCategoryInfo(category: Category): CategoryInfo {
    return when (category) {
        Category.GENERAL_KNOWLEDGE -> CategoryInfo(
            "General Knowledge",
            "Covers a wide range of topics from everyday life, including facts, trivia, and useful information about various subjects such as history, geography, and current events. Ideal for users looking for quick answers or explanations to general questions."
        )
        Category.EDUCATION_AND_LEARNING -> CategoryInfo(
            "Education and Learning",
            "Focuses on educational content, learning techniques, and resources for students and lifelong learners. Topics might include study tips, educational theories, and insights into different learning methods, as well as discussions on educational policy."
        )
        Category.TRAVEL_AND_ADVENTURE -> CategoryInfo(
            "Travel and Adventure",
            "Explores destinations around the world, travel tips, cultural experiences, and adventure travel. Suitable for users planning trips, seeking travel advice, or interested in learning about new and exciting locations and activities."
        )
        Category.COMEDY -> CategoryInfo(
            "Comedy",
            "Involves humorous content, jokes, amusing anecdotes, and light-hearted entertainment. Perfect for users looking to enjoy a laugh, discover comedic insights, or explore the lighter side of life."
        )
        Category.SCIENCE_AND_TECHNOLOGY -> CategoryInfo(
            "Science and Technology",
            "Deals with the latest developments, trends, and insights in science and technology. Topics can include breakthroughs in research, technological innovations, and discussions on the impact of technology on society."
        )
        Category.FOOD_AND_COOKING -> CategoryInfo(
            "Food and Cooking",
            "Offers recipes, cooking techniques, culinary tips, and discussions about food from various cultures. Ideal for home cooks, food enthusiasts, or anyone interested in expanding their culinary skills and knowledge."
        )
        Category.HEALTH_AND_WELLNESS -> CategoryInfo(
            "Health and Wellness",
            "Provides advice on physical health, mental well-being, fitness, nutrition, and wellness practices. Useful for users seeking to improve their health, learn about wellness trends, or find motivation for a healthier lifestyle."
        )
        Category.BOOKS -> CategoryInfo(
            "Books",
            "Focuses on literature, book reviews, reading recommendations, and discussions about authors and literary genres. A great resource for book lovers, readers looking for new books, or discussions on literary themes and developments."
        )
    }
}