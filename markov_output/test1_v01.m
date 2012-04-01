markov = importdata('E:\\Dev\\Java\\Eclipse_3.7.1\\workspace_3.7.1\\Markov\\markov_output\\test1\\mout.txt');
greedy = importdata('E:\\Dev\\Java\\Eclipse_3.7.1\\workspace_3.7.1\\Markov\\markov_output\\test1\\gout.txt');

t_markov = markov(1,:);
action_markov = markov(2,:);
reward_markov = markov(3,:);
price_markov = markov(4,:);
time_markov = markov(5,:);

t_greedy = greedy(1,:);
action_greedy = greedy(2,:);
reward_greedy = greedy(3,:);
price_greedy = greedy(4,:);
time_greedy = greedy(5,:);

m_action = importdata('E:\\Dev\\Java\\Eclipse_3.7.1\\workspace_3.7.1\\Markov\\markov_output\\test1\\m_action.txt');
g_action = importdata('E:\\Dev\\Java\\Eclipse_3.7.1\\workspace_3.7.1\\Markov\\markov_output\\test1\\g_action.txt');

t1 = m_action(1,:);
a1 = m_action(2,:);
t2 = g_action(1,:);
a2 = g_action(2,:);

t_markov = t1;
t_greedy = t2;

subplot(2, 2, 1);
plot(t1, a1, 'r-*', t2, a2, 'b:o');
xlabel('t');
ylabel('Action');
legend('Markov', 'Greedy');
x_size = (max(max(t1), max(t2)))+2;
axis([-2, x_size, 0, max(max(a1), max(a2))+2]);


% subplot(2, 2, 1);
% plot(t_markov, action_markov, 'r*', t_greedy, action_greedy, 'b^');
% xlabel('t');
% ylabel('Action');
% legend('Markov', 'Greedy');
% axis([0, length(t), 0, max(max(a_m), max(a_g))+2]);

subplot(2, 2, 2);
plot(t_markov, reward_markov, 'r-*', t_greedy, reward_greedy, 'b:o');
xlabel('t');
ylabel('Reward');
legend('Markov', 'Greedy');
y_size = min(min(reward_markov), min(reward_greedy)) - 10;
% axis([-2, x_size , y_size, 10]);

subplot(2, 2, 3);
plot(t_markov, price_markov, 'r-*', t_greedy, price_greedy, 'b:o');
xlabel('t');
ylabel('Price Cost');
legend('Markov', 'Greedy');
% x_size = (max(max(t1), max(t2)))+2;
y_size = min(min(price_markov), min(price_greedy)) - 10;
% axis([-2, x_size , y_size, 10]);

subplot(2, 2, 4);
plot(t_markov, time_markov, 'r-*', t_greedy, time_greedy, 'b:o');
xlabel('t');
ylabel('Time Cost');
legend('Markov', 'Greedy');
% x_size = (max(max(t1), max(t2)))+2;
y_size = min(min(time_markov), min(time_greedy)) - 10;
% axis([-2, x_size , y_size, 10]);