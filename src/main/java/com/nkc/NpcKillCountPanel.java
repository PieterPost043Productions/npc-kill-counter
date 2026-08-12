package com.nkc;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Map;
import java.util.function.Consumer;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.PluginPanel;

public class NpcKillCountPanel extends PluginPanel
{
	private final Consumer<String> onResetNpc;
	private final Runnable onResetAll;

	private final JPanel listPanel = new JPanel();

	public NpcKillCountPanel(Consumer<String> onResetNpc, Runnable onResetAll)
	{
		super(false);

		this.onResetNpc = onResetNpc;
		this.onResetAll = onResetAll;

		setLayout(new BorderLayout());
		setBackground(ColorScheme.DARK_GRAY_COLOR);

		JPanel header = new JPanel(new BorderLayout());
		header.setBackground(ColorScheme.DARK_GRAY_COLOR);
		header.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));

		JLabel title = new JLabel("NPC Kill Counter");
		title.setForeground(Color.WHITE);

		JButton resetAllButton = new JButton("Reset all");
		resetAllButton.addActionListener(e -> onResetAll.run());

		header.add(title, BorderLayout.WEST);
		header.add(resetAllButton, BorderLayout.EAST);

		listPanel.setLayout(new GridBagLayout());
		listPanel.setBackground(ColorScheme.DARK_GRAY_COLOR);

		JScrollPane scrollPane = new JScrollPane(listPanel);
		scrollPane.setBorder(BorderFactory.createEmptyBorder());
		scrollPane.getVerticalScrollBar().setUnitIncrement(16);

		add(header, BorderLayout.NORTH);
		add(scrollPane, BorderLayout.CENTER);
	}

	public void rebuild(Map<String, Integer> counts)
	{
		listPanel.removeAll();

		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(0, 0, 4, 0);

		if (counts.isEmpty())
		{
			JLabel empty = new JLabel("No kills recorded yet.");
			empty.setForeground(Color.LIGHT_GRAY);
			listPanel.add(empty, c);
		}
		else
		{
			for (Map.Entry<String, Integer> entry : counts.entrySet())
			{
				listPanel.add(buildRow(entry.getKey(), entry.getValue()), c);
				c.gridy++;
			}
		}

		revalidate();
		repaint();
	}

	private JPanel buildRow(String name, int count)
	{
		JPanel row = new JPanel(new BorderLayout(4, 0));
		row.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		row.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));

		JLabel nameLabel = new JLabel(name);
		nameLabel.setForeground(Color.WHITE);

		JLabel countLabel = new JLabel(String.valueOf(count));
		countLabel.setForeground(ColorScheme.BRAND_ORANGE);
		countLabel.setHorizontalAlignment(SwingConstants.CENTER);
		countLabel.setPreferredSize(new Dimension(36, 20));

		JButton resetButton = new JButton("x");
		resetButton.setToolTipText("Reset kills for " + name);
		resetButton.setMargin(new Insets(0, 4, 0, 4));
		resetButton.addActionListener(e -> onResetNpc.accept(name));

		JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 0));
		right.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		right.add(countLabel);
		right.add(resetButton);

		row.add(nameLabel, BorderLayout.CENTER);
		row.add(right, BorderLayout.EAST);

		return row;
	}
}
