package com.os.bakingapp.widgets;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RemoteViews;

import com.google.gson.Gson;
import com.looptech.bakingapp.R;
import com.os.bakingapp.activities.RecipeDetailsActivity;
import com.os.bakingapp.models.Recipe;
import com.squareup.picasso.Picasso;

import static com.os.bakingapp.widgets.RecipeIngredientsListRemoteViewsFactory.WIDGET_PREFS_NAME;
import static com.os.bakingapp.widgets.RecipeIngredientsListRemoteViewsFactory.WIDGET_RECIPE_KEY;

/**
 * Implementation of App Widget functionality.
 */
public class RecipesWidget extends AppWidgetProvider {

    public static void updateAllWidgets(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        String json = context.getSharedPreferences(WIDGET_PREFS_NAME, Context.MODE_PRIVATE)
                .getString(WIDGET_RECIPE_KEY, null);
        Gson gson = new Gson();

        Recipe recipe = gson.fromJson(json, Recipe.class);

        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId, recipe);
        }
    }

    private static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Recipe recipe) {
        appWidgetManager.updateAppWidget(appWidgetId, getRecipeRemoteViews(context, recipe, appWidgetId));
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        updateAllWidgets(context, appWidgetManager, appWidgetIds);
    }

    private static RemoteViews getRecipeRemoteViews(Context context, Recipe recipe, int appWidgetId) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.recipe_widget);

        views.setTextViewText(R.id.widgetRecipeName, recipe.recipeName);

        Intent appIntent = new Intent(context, RecipeDetailsActivity.class);
        appIntent.putExtra(RecipeDetailsActivity.RECIPE_EXTRA, recipe);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, appIntent, 0);

        Picasso.get()
                .load(recipe.imageUrl)
                .resize(1000, 1000)
                .centerCrop()
                .into(views, R.id.widgetRecipeImage, new int[]{appWidgetId});

        views.setOnClickPendingIntent(R.id.widgetRecipeContainer, pendingIntent);

        Intent intent = new Intent(context, RecipeIngredientsListRemoteViewsService.class);
        views.setRemoteAdapter(R.id.widgetRecipeIngredients, intent);

        return views;
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager,
                                          int appWidgetId, Bundle newOptions) {
        updateAllWidgets(context, appWidgetManager, appWidgetManager.getAppWidgetIds(new ComponentName(context, RecipesWidget.class)));
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
    }

    @Override
    public void onEnabled(Context context) {

    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

