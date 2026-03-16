package com.example.tick_tack_toe_infinity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import android.content.Intent;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class GameModeAdapter extends RecyclerView.Adapter<GameModeAdapter.GameModeViewHolder> {

    // Внутренний класс для хранения данных одного элемента
    public static class GameMode {
        String title;
        int iconResId;

        public GameMode(String title, int iconResId) {
            this.title = title;
            this.iconResId = iconResId;
        }
    }

    private List<GameMode> gameModes;

    public GameModeAdapter(List<GameMode> gameModes) {
        this.gameModes = gameModes;
    }

    @NonNull
    @Override
    public GameModeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Создаем View из нашего XML-макета item_game_mode.xml
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_game_mode, parent, false);
        return new GameModeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GameModeViewHolder holder, int position) {
        // Заполняем View данными для текущей позиции
        GameMode mode = gameModes.get(position);
        holder.tvTitle.setText(mode.title);
        holder.ivIcon.setImageResource(mode.iconResId);

        // --- НАЧАЛО ИЗМЕНЕНИЙ ---

        // Устанавливаем обработчик нажатия на всю карточку
        holder.itemView.setOnClickListener(v -> {
            // Получаем контекст из View, на которую нажали
            // ПРАВИЛЬНО
            Context context = v.getContext();


            // Проверяем позицию карточки, на которую нажал пользователь.
            // Согласно вашему коду в MenuActivity, позиции такие:
            // 0 - "Игрок vs Игрок"
            // 1 - "Игрок vs Компьютер"
            // 2 - "История игр"

            // Нас интересует "Игрок vs Компьютер"
            if (position == 1) {
                // 1. Создаем Intent для запуска GameActivity
                Intent intent = new Intent(context, DifficultyMenuActivity.class);
                context.startActivity(intent);

                // 2. (Опционально) Можно передать дополнительную информацию на игровой экран.
                // Например, какой режим игры мы выбрали.
                intent.putExtra("GAME_MODE", "PLAYER_VS_AI");

                // 3. Запускаем GameActivity
                context.startActivity(intent);
            } else if (position == 0) {
                // Здесь можно будет обработать нажатие на "Игрок vs Игрок"
                // Например, показать уведомление, что режим в разработке
                Toast.makeText(context, "Режим 'Игрок vs Игрок' в разработке", Toast.LENGTH_SHORT).show();
            } else if (position == 2) {
                // Здесь будет обработка нажатия на "Историю игр"
                Toast.makeText(context, "Экран истории в разработке", Toast.LENGTH_SHORT).show();
            }
        });

        // --- КОНЕЦ ИЗМЕНЕНИЙ ---
    }

    @Override
    public int getItemCount() {
        // Возвращаем общее количество элементов
        return gameModes.size();
    }

    // ViewHolder - класс, который "держит" ссылки на элементы UI одной карточки
    static class GameModeViewHolder extends RecyclerView.ViewHolder {
        ImageView ivIcon;
        TextView tvTitle;

        public GameModeViewHolder(@NonNull View itemView) {
            super(itemView);
            ivIcon = itemView.findViewById(R.id.ivGameModeIcon);
            tvTitle = itemView.findViewById(R.id.tvGameModeTitle);
        }
    }
}

